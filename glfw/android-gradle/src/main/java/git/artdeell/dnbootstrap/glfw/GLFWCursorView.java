package git.artdeell.dnbootstrap.glfw;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import git.artdeell.dnbglfw.R;


public class GLFWCursorView extends View implements CursorImplementor {
    private Drawable cursorDrawable;
    private Drawable defaultCursorDrawable;
    private final Paint customCursorPaint = new Paint();
    private boolean noDraw = false;
    private float mouseScale = 1f;
    private int hotX = 0, hotY = 0;

    public GLFWCursorView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public GLFWCursorView(Context context) {
        this(context, null);
    }

    public GLFWCursorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GLFWCursorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        GLFW.setCursorImpl(this);
        if(attrs != null) {
            try(TypedArray arr = context.obtainStyledAttributes(attrs,R.styleable.GLFWCursorView)) {
                cursorDrawable = arr.getDrawable(R.styleable.GLFWCursorView_defaultCursorDrawable);
            }
        }
        if(cursorDrawable == null) cursorDrawable = new FallbackCursorDrawable();
        defaultCursorDrawable = cursorDrawable;
        cursorDrawable.setBounds(0, 0, 36, 54);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if(noDraw) return;
        canvas.translate((int)(GLFW.cursorX * getWidth()), (int)(GLFW.cursorY * getHeight()));
        GLFWCursor cursor = GLFW.getCursor();
        canvas.scale(mouseScale, mouseScale);
        if(cursor == null) {
            canvas.translate(-hotX, -hotY);
            cursorDrawable.draw(canvas);
        }else {
            canvas.drawBitmap(cursor.bitmap, -cursor.hotX, -cursor.hotY, customCursorPaint);
        }
    }

    @Override
    public void onCursorPosition() {
        if(!noDraw) post(this::invalidate);
    }

    @Override
    public void onCursorChanged() {
        post(this::invalidate);
    }

    @Override
    public void onGrabState(boolean isGrabbing) {
        noDraw = isGrabbing;
        invalidate();
    }

    public void setCursorScale(float scale){
        this.mouseScale = scale;
    }

    /**
     * Set a custom cursor drawable and hotspot.
     * @param drawable The drawable to use, or null to reset to default.
     * @param hotXPerc Hotspot X as percentage (0-100).
     * @param hotYPerc Hotspot Y as percentage (0-100).
     */
    public void setCursor(Drawable drawable, int hotXPerc, int hotYPerc) {
        this.cursorDrawable = drawable != null ? drawable : defaultCursorDrawable;
        if(this.cursorDrawable != null) {
            int width = this.cursorDrawable.getIntrinsicWidth();
            int height = this.cursorDrawable.getIntrinsicHeight();
            if (width <= 0 || height <= 0) {
                width = 36;
                height = 54;
            } else {
                float ratio = (float) width / height;
                // Normalize height to 54, which is the default cursor height
                height = 54;
                width = (int) (height * ratio);
            }
            this.cursorDrawable.setBounds(0, 0, width, height);
            
            if (drawable != null) {
                this.hotX = (int) ((hotXPerc / 100f) * width);
                this.hotY = (int) ((hotYPerc / 100f) * height);
            } else {
                this.hotX = 0;
                this.hotY = 0;
            }
        }
        post(this::invalidate);
    }
}
