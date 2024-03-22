package edu.northeastern.new_final;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CircularImageView extends AppCompatImageView {

    private Paint backgroundPaint;
    private Paint borderPaint;
    private Path clipPath;

    public CircularImageView(Context context) {
        super(context);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.WHITE); // Set your desired background color
        backgroundPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#9acc69")); // Set your desired border color
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(5); // Set your desired border width


        clipPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2f;


        // Draw colored border
        canvas.drawCircle(width / 2f, height / 2f, radius, borderPaint);


        // Draw circular background
        canvas.drawCircle(width / 2f, height / 2f, Math.min(width, height) / 2f, backgroundPaint);

        // Clip the canvas to a circular shape
        clipPath.reset();
        clipPath.addCircle(width / 2f, height / 2f, Math.min(width, height) / 2f, Path.Direction.CW);
        canvas.clipPath(clipPath);

        // Draw the image
        Drawable drawable = getDrawable();
        if (drawable != null) {
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
        }
    }
}
