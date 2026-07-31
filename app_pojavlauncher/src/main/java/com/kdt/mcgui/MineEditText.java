package com.kdt.mcgui;

import android.content.*;
import android.util.*;
import androidx.core.content.ContextCompat;
import net.ashmeet.hyperlauncher.R;
import net.kdt.pojavlaunch.Tools;

public class MineEditText extends androidx.appcompat.widget.AppCompatEditText {
	public MineEditText(Context ctx) {
		super(ctx);
		init();
	}

	public MineEditText(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init();
	}

	public void init() {
		setBackgroundColor(ContextCompat.getColor(getContext(), R.color.edit_text_background));
		setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
		setHintTextColor(ContextCompat.getColor(getContext(), R.color.secondary_text));
		int padding = (int) Tools.dpToPx(8);
		setPadding(padding, padding, padding, padding);
	}
}
