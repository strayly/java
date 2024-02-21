#-*- encoding: utf-8 -*-
#读取邮件导入到mysql数据库
import imaplib
import email
import MySQLdb
import MySQLdb.cursors
import time
#设置命令窗口输出使用中文编码
import sys
import os
import re
reload(sys)
sys.setdefaultencoding('gbk')

import ConfigParser

cp = ConfigParser.SafeConfigParser()
cp.read('my.conf')
att_path = cp.get('email_att', 'att_path')

def filter_html(s):  
    if s==None:
        return ''
    if(len(s)>10):

        d = re.compile(r'<script(.*?)</script>',re.S|re.IGNORECASE)  
        s = d.sub(' ',s)
        d = re.compile(r'<iframe(.*?)</iframe>',re.S|re.IGNORECASE)  
        s = d.sub(' ',s)
        d = re.compile(r'<style(.*?)</style>',re.S|re.IGNORECASE)  
        s = d.sub(' ',s)
        d = re.compile(r'<!--(.*?)-->',re.S|re.IGNORECASE)  
        s = d.sub(' ',s)
        #d = re.compile(r'<[^>]+>',re.S|re.IGNORECASE)  
        #s = d.sub(' ',s)

    return s  
#保存文件方法（都是保存在指定的根目录下）
def savefile(filename, data, path):

    filepath = path + filename
    #print 'Saved as ' + filepath
    f = open(filepath, 'wb')
    f.write(data)
    f.close()

#字符编码转换方法
def my_unicode(s, encoding):
    if encoding:
        return unicode(s, encoding)
    else:
        return unicode(s)

#获得字符编码方法
def get_charset(message, default="ascii"):
    #Get themessage charset
    charset=message.get_charset()
    return charset
    '''
    if charset==None:
        return default
    else :
        return charset
    '''

#解析邮件方法（区分出正文与附件）
def parseEmail(msg, mychar,path):
    mailContent= None
    contenttype= None
    ym = time.strftime('%Y%m%d',time.localtime(time.time()))
    mypath = path+"/"+str(ym)+"/"
    if os.path.exists(mypath)==False:
      os.mkdir(mypath)
    atts = ''
    attarr = []
    suffix=None
    for part in msg.walk():
       if not part.is_multipart():
           contenttype =part.get_content_type()
           filename = part.get_filename()
           charset = get_charset(part)
           #是否有附件
           if filename:
               h = email.Header.Header(filename)
               dh = email.Header.decode_header(h)
               fname = dh[0][0]
               encodeStr = dh[0][1]
               if encodeStr != None:
                   if charset == None:
                       fname = fname.decode(encodeStr, 'gbk')
                   else:
                       fname = fname.decode(encodeStr, charset)
               data = part.get_payload(decode=True)
               #print('Attachment : ' + fname)
               #保存附件
               if fname != None or fname != '':
                    savefile(fname+".att", data,mypath)
                    attarr.append(fname)
           else:
               if contenttype in ['text/plain']:
                   suffix = '.txt'
               if contenttype in ['text/html']:
                   suffix = '.htm'
               if charset == None:
                    if mychar!=None:mailContent =part.get_payload(decode=True).decode(mychar)

                    else:mailContent = part.get_payload(decode=True)

               else:
                   mailContent =part.get_payload(decode=True).decode(charset)
    atts = ';'.join(attarr)
    return  (mailContent, suffix,atts)
def get_line_char(s):
    arr = s.split('?')
    if len(arr)>3 and arr[2].lower()=='b':return arr[1]
    else:return None
#获取邮件方法
def getMail(cfgname, attpath):
    mailhost = cp.get(cfgname, 'imaphost')
    mailhost = cp.get(cfgname, 'imaphost')
    password = cp.get(cfgname, 'psswd')
    account = cp.get(cfgname, 'account')
    password = cp.get(cfgname, 'psswd')
    port = int(cp.get(cfgname, 'port'))
    ssl = int(cp.get(cfgname, 'ssl'))
    typeid = int(cp.get(cfgname, 'typeid'))
   #是否采用ssl
    if ssl ==1:
       imapServer = imaplib.IMAP4_SSL(mailhost, port)
    else:
       imapServer = imaplib.IMAP4(mailhost, port)
    imapServer.login(account, password)
    imapServer.select()
   #邮件状态设置，新邮件为Unseen
    #Messagestatues = 'All,Unseen,Seen,Recent,Answered, Flagged'
    resp, items= imapServer.search(None, "Unseen")
    midarr = items[0].split()
    midarr.reverse()
    number =1
    for i in midarr:
      #get information of email
      resp, mailData = imapServer.fetch(i,"(RFC822)")
      mailText = mailData[0][1]
      msg = email.message_from_string(mailText)

      subject = email.Header.decode_header(msg["Subject"])
      subchar = subject[0][1]
      if subchar==None:subchar=get_line_char(msg["Subject"])
      sub = my_unicode(subject[0][0],subchar).encode('gbk')
      strsub = 'Subject : ' + sub


      ls = msg["From"].split(' ')
      strfrom = ''
      if(len(ls) == 2):
          fromname = email.Header.decode_header((ls[0]).strip('\"'))
          strfrom = 'From : ' + my_unicode(fromname[0][0], fromname[0][1]).encode('gbk') +ls[1]
      else:
          strfrom = 'From : ' + msg["From"].encode('gbk')
      strdate = 'Date : ' + msg["Date"]

      #print(msg["X-QQ-mid"])
      msgid = msg["X-QQ-mid"]
      fromstr = msg["From"]
      fromarr = fromstr.split('<')
      fromstr2 = fromarr[len(fromarr)-1]
      fromarr2 = fromstr2.split('>')
      fromemail = fromarr2[0]
      mailContent, suffix ,atts= parseEmail(msg, subchar,attpath)
      #命令窗体输出邮件基本信息

      #保存邮件正文
      
      if (suffix != None and suffix != '') and (mailContent != None and mailContent != ''):
          mailContent = filter_html(mailContent)
          #savefile(str(number) + suffix, mailContent, mypath)
          save_to_db(cp,msgid,sub,mailContent,fromemail,atts,typeid)
      number = number + 1
      if number>2:break

    imapServer.close()
    imapServer.logout()



if __name__ =="__main__":
    getMail('email_info', att_path)

