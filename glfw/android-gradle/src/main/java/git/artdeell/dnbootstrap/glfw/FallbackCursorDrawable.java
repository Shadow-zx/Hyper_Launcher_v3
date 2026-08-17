package git.artdeell.dnbootstrap.glfw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FallbackCursorDrawable extends Drawable {
    private final Paint fallbackPaint = new Paint();
    private final Path cursorPath = new Path();

    public FallbackCursorDrawable() {
        fallbackPaint.setStyle(Paint.Style.FILL);
        fallbackPaint.setColor(Color.WHITE);
        fallbackPaint.setAntiAlias(true);
        // Add a small shadow/stroke for visibility on white backgrounds
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float w = getBounds().width();
        float h = getBounds().height();
        
        cursorPath.reset();
        cursorPath.moveTo(0, 0);
        cursorPath.lineTo(w, h * 0.7f);
        cursorPath.lineTo(w * 0.5f, h * 0.7f);
        cursorPath.lineTo(w * 0.5f, h);
        cursorPath.close();

        // Draw shadow first
        fallbackPaint.setColor(Color.BLACK);
        canvas.save();
        canvas.translate(2, 2);
        canvas.drawPath(cursorPath, fallbackPaint);
        canvas.restore();

        // Draw white cursor
        fallbackPaint.setColor(Color.WHITE);
        canvas.drawPath(cursorPath, fallbackPaint);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int i) {
        fallbackPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        fallbackPaint.setColorFilter(colorFilter);
    }
}
