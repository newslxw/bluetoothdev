package com.alensic.nursing.mobile.ui.titlebar;

import com.alensic.nursing.mobile.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义控件，需要attrs.xml配置文件
 * @author xwlian
 *
 */
public class TitleBarView extends LinearLayout {

	private Button btn_left;
	private Button btn_right;
	private TextView tv_title;
	private String strBtnLeft;
	private String strBtnRight;
	private String strTitle;
	private int left_drawable;
	private int right_drawable;
	private  static int fontColor=Color.WHITE;
	private  static float fontSize_btn = 16; 
	private  static float fontSize_title = 20; 

	public TitleBarView(Context context) {
		super(context);
		initContent(context);
	}

	public TitleBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttributes(attrs);
		initContent(context);
	}

	private void initAttributes(AttributeSet attributeSet) {

		if (null != attributeSet) {

			final int attrIds[] = new int[] { R.attr.btn_leftText,
					R.attr.btn_rightText, R.attr.tv_title,
					R.attr.left_drawable, R.attr.right_drawable };

			Context context = getContext();

			TypedArray array = context.obtainStyledAttributes(attributeSet,
					attrIds);

			CharSequence t1 = array.getText(0);
			CharSequence t2 = array.getText(1);
			CharSequence t3 = array.getText(2);
			left_drawable = array.getResourceId(3, 0);
			right_drawable = array.getResourceId(4, 0);

			array.recycle();

			if (null != t1) {
				strBtnLeft = t1.toString();
			}
			if (null != t2) {
				strBtnRight = t2.toString();

			}
			if (null != t3) {
				strTitle = t3.toString();
			}

		}

	}

	private void initContent(Context context) {

		Log.i("coder", "-----initContent----");
		// 设置水平方向
		setOrientation(HORIZONTAL);

		setGravity(Gravity.CENTER_VERTICAL);
		// 设置背景
		setBackgroundResource(R.drawable.ic_title_bg);

		//Context context = getContext();

		btn_left = new Button(context);
		btn_left.setVisibility(View.INVISIBLE);// 设置设置不可见
		if (left_drawable != 0) {
			btn_left.setBackgroundResource(left_drawable);
		} else {

			btn_left.setBackgroundResource(R.drawable.button_back);// 设置背景
		}
		btn_left.setTextColor(fontColor);// 字体颜色
		//btn_left.setTextAppearance(context, android.R.attr.textAppearanceSmall);
		if (null != strBtnLeft) {
			LayoutParams btnLeftParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			btnLeftParams.setMargins(10, 0, 0, 0);
			btn_left.setLayoutParams(btnLeftParams);
			btn_left.setText(strBtnLeft);
			btn_left.setVisibility(View.VISIBLE);
		} else {
			btn_left.setLayoutParams(new LayoutParams(50, 50));
		}

		btn_left.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize_btn);
		// 添加这个按钮
		addView(btn_left);

		//
		tv_title = new TextView(context);

		LayoutParams centerParam = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		centerParam.weight = 1;
		tv_title.setLayoutParams(centerParam);
		tv_title.setTextColor(fontColor);
		//tv_title.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Medium);

		tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize_title);
		if (null != strTitle) {
			tv_title.setText(strTitle);
		}

		tv_title.setGravity(Gravity.CENTER);
		btn_left.setVisibility(View.VISIBLE);
		// 添加这个标题
		addView(tv_title);

		btn_right = new Button(context);
		btn_right.setVisibility(View.INVISIBLE);// 设置设置不可见
		btn_right.setBackgroundResource(R.drawable.button_next);// 设置背景
		btn_right.setTextColor(fontColor);// 字体颜色
		btn_right.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize_btn);
		//btn_right.setTextAppearance(context, android.R.attr.textAppearanceSmall);

		if (right_drawable != 0) {
			btn_right.setBackgroundResource(right_drawable);
		} else {

			btn_right.setBackgroundResource(R.drawable.button_next);// 设置背景
		}
		if (null != strBtnRight) {

			LayoutParams btnRightParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			btnRightParams.setMargins(0, 0, 10, 0);
			btn_right.setLayoutParams(btnRightParams);

			btn_right.setText(strBtnRight);
			btn_right.setVisibility(View.VISIBLE);
		} else {
			btn_right.setLayoutParams(new LayoutParams(50, 50));
		}

		// 添加这个按钮
		addView(btn_right);

	}

	public Button getBtn_left() {
		return btn_left;
	}

	public Button getBtn_right() {
		return btn_right;
	}

	public TextView getTv_title() {
		return tv_title;
	}

	public String getStrBtnLeft() {
		return strBtnLeft;
	}

	public void setStrBtnLeft(String strBtnLeft) {
		this.strBtnLeft = strBtnLeft;
	}

	public String getStrBtnRight() {
		return strBtnRight;
	}

	public void setStrBtnRight(String strBtnRight) {
		this.strBtnRight = strBtnRight;
	}

	public String getStrTitle() {
		return strTitle;
	}

	public void setStrTitle(String strTitle) {
		this.strTitle = strTitle;
	}

	public int getLeft_drawable() {
		return left_drawable;
	}

	public void setLeft_drawable(int left_drawable) {
		this.left_drawable = left_drawable;
	}

	public int getRight_drawable() {
		return right_drawable;
	}

	public void setRight_drawable(int right_drawable) {
		this.right_drawable = right_drawable;
	}

}