package com.rainnka.ZHNews.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.rainnka.ZHNews.Activity.Base.SwipeBackAty;
import com.rainnka.ZHNews.R;

/**
 * Created by rainnka on 2016/7/23 13:48
 * Project name is ZHKUNews
 */
public class FeedbackAty extends SwipeBackAty {

	public Toolbar toolbar;
	public ImageView imageView_sent;
	public EditText editText_subject;
	public EditText editText_text;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//			Window window = getWindow();
//			// Translucent status bar
//			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager
//					.LayoutParams.FLAG_TRANSLUCENT_STATUS);
////			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager
////					.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}
		setContentView(R.layout.feedback_act);
		setupWindowAnimations();

		/*
		* 另statusbar悬浮于activity上面
		* */
		getWindow().getDecorView().setSystemUiVisibility(View
				.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

		initComponent();

		initToolbar();

		addSentIVOnClickListener();
	}

	private void setupWindowAnimations() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			//			Fade fade = new Fade();
			//			fade.setDuration(500);
			//			Explode explode = new Explode();
			//			explode.setDuration(300);
			Slide slide = new Slide();
			slide.setSlideEdge(Gravity.RIGHT);
			slide.setDuration(300);
			getWindow().setEnterTransition(slide);
			//			getWindow().setReturnTransition(slide);
		}
	}

	private void addSentIVOnClickListener() {
		imageView_sent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO);
				intent.setData(Uri.parse("mailto:hennzr@gmail.com"));
				intent.putExtra(Intent.EXTRA_SUBJECT, editText_subject.getText().toString());
				intent.putExtra(Intent.EXTRA_TEXT, editText_text.getText().toString());
				startActivity(intent);
			}
		});
	}

	private void initComponent() {
		toolbar = (Toolbar) findViewById(R.id.Feedback_act_Toolbar);
		imageView_sent = (ImageView) findViewById(R.id.Feedback_act_ImageView_sent);
		editText_subject = (EditText) findViewById(R.id.Feedback_act_ET_subject);
		editText_text = (EditText) findViewById(R.id.Feedback_act_ET_text);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		getSwipeBackLayout().scrollToFinishActivity();
	}

	private void initToolbar() {
		toolbar.setTitle("反馈");
		toolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
