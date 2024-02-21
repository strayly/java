package com.xiaozhao.util;

import java.util.ArrayList;

import com.xiaozhao.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/***
 * 组合控件
 * 
 */
public class RadioButton extends LinearLayout {
	private Context context;
	private ImageView imageView;
	private TextView textView,errorView;

	private int index = 0;
	private int id = 0;// 判断是否选中
	private int pos = 0;
	private int status = 0;
	
	private RadioButton tempRadioButton;// 模版用于保存上次点击的对象

	private int state[] = { R.drawable.radio_unchecked,
			R.drawable.radio_checked };


	/***
	 * 改变图片
	 */
	public void ChageImage() {

		index++;
		id = index % 2;// 获取图片id
		imageView.setImageResource(state[id]);
		
	}

	public void setChecked() {
		imageView.setImageResource(1);
	}
	public void setUnchecked() {
		imageView.setImageResource(0);
	}
	
	/***
	 * 设置文本
	 * 
	 * @param text
	 */
	public void setText(String text) {
		textView.setText(text);
	}

	public String getText() {
		return id == 0 ? "" : textView.getText().toString();
	}
	
	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getPos() {
		return pos;
	}

	
	public RadioButton(Context context) {
		this(context, null);

	}

	public RadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.radioitem, this, true);
		imageView = (ImageView) findViewById(R.id.iv_radio_img);
		textView = (TextView) findViewById(R.id.tv_radio_tx);
		//errorView = (TextView) findViewById(R.id.tv_error_tx);

	}

}
