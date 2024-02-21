#采集 对base64 zip加密的解码处理
        
def decode_content(content,domain):

    html_content_arr = content.split('Base64.decode(unzip("')
    decode_html = content
    i = 0

    if len(html_content_arr)>1:
        for item in html_content_arr:
            if i>0:
                html_content_arr2 = item.split('").substr(')
                j = 0
                if len(html_content_arr2)==2:
                    #decode_html+=decode_content(html_content_arr2[0],domain)
                    sarr2 = ('").substr('+html_content_arr2[1]).split('")') 
                    sstr = html_content_arr2[0]
                    sarr3 = sarr2[1].split(');') 
                    sarr4 = sarr3[0].split('substr(') 
                    c1 = 0
                    c2 = 0
                    c1_str = sarr4[1].replace(')','') 
                    c1_str = c1_str.replace('.','') 
                    c1_str = c1_str.replace('(','')
                    c1 = int(c1_str)
                    c2_str = sarr4[2].replace(')','')
                    c2_str = c2_str.replace('.','') 
                    c2_str = c2_str.replace('(','')
                    c2 = int(c2_str);
                    
                    s = base64.b64decode(sstr)
                    s = zlib.decompress(s) #;// gzdeflate('Compress me', 6, ZLIB_ENCODING_DEFLATE )
                    s = s[c1:] 
                    s = base64.b64decode(s);
                    s = s[c2:]
                    istr = str(i)
                    decode_html+="\r\nDECODE_START_"+istr+"::"+s.decode('utf8',"ignore")+"::DECODE_END_"+istr
            i = i+1
        #decode_html+=html_content_arr[1]

    '''
	preg_match_all("/<\s*img\s+[^>]*?src\s*=\s*(\'|\")(.*?)\\1[^>]*?\/?\s*>/i",$s,$match_img);
	
	$i = 0;		
	if($match_img[2] && is_array($match_img[2])){
		usort($match_img[2], "cmp_len");
		foreach($match_img[2] as $img_val){
			//$img_tmp = "#img_".$i."#";
			$s = str_replace($img_val,$domain.$img_val,$s);
			$i++;
		}
	}
    '''
    return decode_html
