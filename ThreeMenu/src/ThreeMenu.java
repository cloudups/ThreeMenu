
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnticipateOvershootInterpolator;

/**
 * 
 * @author feiqishi
 * github:  https://github.com/cloudups/ThreeMenu
 * 
 */
public class ThreeMenu extends View implements OnClickListener {

	/**
	 * view的宽和高
	 */
	private int viewWidth;
	private int viewHeight;

	/**
	 * 线条长度
	 */
	private int lineWidth;

	/**
	 * 当线1和线2移动时始末坐标的便宜量
	 */
	private int line1and3XDst;
	private int line1and3YDst;
	/**
	 * 三条线段的起点和终点坐标
	 */
	private Point line1Start, line1End;
	private Point line2Start, line2End;
	private Point line3Start, line3End;

	/**
	 * 第一条线段距离第三条线段的距离
	 */
	private int line1to3Height;

	/**
	 * 控制中间线段的path
	 */
	private Path p1, p2, p3, p;
	private PathMeasure p1Measure, p2Measure, p3Measure, pMeasure;

	/**
	 * 动画效果
	 */
	private ValueAnimator mAnimator;

	/**
	 * 动画进度
	 */
	private Float schedule = 0f;

	/**
	 * 第二条线段截取的起始位置和长度
	 */
	float startD, length;

	/**
	 * 第一条线段和第三条线段的起点临时y坐标，动画过程中不断变化
	 */
	private Point tmp1Start, tmp1End, tmp3Start, tmp3End;
	/**
	 * 动画类型，positive或者negative
	 */
	private Mode mode = Mode.POSTIVE;

	/**
	 * 画笔
	 */
	private Paint paint;

	/**
	 * 动画模式
	 * 
	 */
	enum Mode {
		POSTIVE, NEGATIVE
	}

	public ThreeMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ThreeMenu(Context context) {
		super(context);
	}

	public ThreeMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 必須放在measure之后才能获取高度和宽度
		init();
	}

	private void init() {
		initData();
		initPaint();
		initPath();
		initAnimation();
	}

	/**
	 * 初始化坐标线段信息
	 */
	private void initData() {
		viewWidth = (int) (getMeasuredWidth() * 0.8);
		viewHeight = (int) (getMeasuredHeight() * 0.8);
		lineWidth = (int) (getMeasuredWidth() * 0.5);
		line1and3XDst = lineWidth / 8;

		line1Start = new Point();
		line2Start = new Point();
		line3Start = new Point();

		line1End = new Point();
		line2End = new Point();
		line3End = new Point();

		line1to3Height = viewHeight / 3;
		line2Start.x = (int) (getMeasuredWidth() * 0.25);
		line2Start.y = (int) (getMeasuredWidth() * 0.5);
		line2End.x = line2Start.x + lineWidth;
		line2End.y = line2Start.y;

		line1Start.x = (int) (getMeasuredWidth() * 0.25);
		line1Start.y = (int) (line2Start.y - line1to3Height / 10 * 7);
		line1End.x = line1Start.x + lineWidth;
		line1End.y = line1Start.y;

		line3Start.x = (int) (getMeasuredWidth() * 0.25);
		line3Start.y = (int) (line2Start.y + line1to3Height / 10 * 7);
		line3End.x = line3Start.x + lineWidth;
		line3End.y = line3Start.y;
		line1to3Height = line3Start.y - line1Start.y;
		line1and3YDst = line1to3Height / 10;

		tmp1Start = new Point(line1Start);
		tmp3Start = new Point(line3Start);
		tmp1End = new Point(line1End);
		tmp3End = new Point(line3End);
	}

	/**
	 * 初始化画笔
	 */
	private void initPaint() {
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(line1to3Height / 5);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setAntiAlias(true);
	}

	/**
	 * 初始化路径信息
	 */
	private void initPath() {

		p1 = new Path();
		p2 = new Path();
		p3 = new Path();

		p1.reset();
		p1.moveTo(line2Start.x, line2Start.y);
		p1.lineTo(line2End.x, line2End.y); // 放大镜把手

		RectF rect = new RectF((int) (getMeasuredWidth() * 0.1),
				(int) (getMeasuredHeight() * 0.1),
				(int) (getMeasuredWidth() * 0.9),
				(int) (getMeasuredHeight() * 0.9));

		p3.reset();
		p3.addArc(rect, -30, -359.9f);

		p3Measure = new PathMeasure(p3, false);
		float[] pos = new float[2];
		p3Measure.getPosTan(0, pos, null);

		p2.moveTo(line2End.x, line2End.y);
		p2.quadTo((int) (getMeasuredWidth() * 0.9), line2End.y, pos[0], pos[1]);

		p = new Path();
		p.moveTo(line2Start.x, line2Start.y);
		p.lineTo(line2End.x, line2End.y);
		p.quadTo((int) (getMeasuredWidth() * 0.9), line2End.y, pos[0], pos[1]);
		p.arcTo(rect, -30, -359.9f);

		p1Measure = new PathMeasure(p1, false);
		p2Measure = new PathMeasure(p2, false);
		pMeasure = new PathMeasure(p, false);

		startD = 0;
		length = p1Measure.getLength();

	}

	/**
	 * 实现动画效果
	 */
	private void initAnimation() {
		mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
		mAnimator.setDuration(1000);
		mAnimator.setInterpolator(new AnticipateOvershootInterpolator());
		mAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {

				if (mode == Mode.POSTIVE) {
					schedule = (Float) arg0.getAnimatedValue();
				} else {
					schedule = 1 - (Float) arg0.getAnimatedValue();
				}

				startD = (p1Measure.getLength() + p2Measure.getLength() + p3Measure
						.getLength() / 3) * schedule;

				length = p1Measure.getLength()
						+ (p3Measure.getLength() - p1Measure.getLength())
						* schedule;

				tmp1Start.x = (int) (line1Start.x + line1and3XDst * schedule);
				tmp1Start.y = (int) (line1Start.y + (line1to3Height + line1and3YDst)
						* schedule);
				tmp1End.x = (int) (line1End.x - line1and3XDst * schedule);
				tmp1End.y = (int) (line1End.y - line1and3YDst * schedule);
				tmp3Start.x = (int) (line3Start.x + line1and3XDst * schedule);
				tmp3Start.y = (int) (line3Start.y - (line1to3Height + line1and3YDst)
						* schedule);
				tmp3End.x = (int) (line3End.x - line1and3XDst * schedule);
				tmp3End.y = (int) (line3End.y + line1and3YDst * schedule);

				invalidate();
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {

		/**
		 * 临时路径
		 */
		Path dst = new Path();

		/**
		 * 绘制第一条线
		 */
		dst.moveTo(tmp1Start.x, tmp1Start.y);
		dst.lineTo(tmp1End.x, tmp1End.y);
		canvas.drawPath(dst, paint);
		dst.reset();

		/**
		 * 绘制第三条线
		 */
		dst.moveTo(tmp3Start.x, tmp3Start.y);
		dst.lineTo(tmp3End.x, tmp3End.y);
		canvas.drawPath(dst, paint);
		dst.reset();

		/**
		 * 绘制第二条线，使用插值器，回弹动画效果会产生负值，
		 */
		if (schedule < 0) {
			dst.moveTo(line2Start.x + schedule * 200, line2Start.y);
			dst.lineTo(line2Start.x + schedule * 200 + lineWidth, line2Start.y);
			canvas.drawPath(dst, paint);
		}

		/**
		 * 实现中间线的绘制效果
		 */
		if (pMeasure.getLength() - startD >= length) {

			float[] x = new float[2];
			pMeasure.getPosTan(startD, x, null);
			dst.moveTo(x[0], x[1]);
			dst.lineTo(x[0], x[1]);

			pMeasure.getSegment(startD, startD + length, dst, true);
			canvas.drawPath(dst, paint);
		} else {
			float[] x = new float[2];
			pMeasure.getPosTan(startD, x, null);
			dst.moveTo(x[0], x[1]);
			dst.lineTo(x[0], x[1]);

			pMeasure.getSegment(
					startD,
					p1Measure.getLength() + p2Measure.getLength()
							+ p3Measure.getLength(), dst, true);
			p3Measure.getSegment(0, length - (pMeasure.getLength() - startD),
					dst, false);
			canvas.drawPath(dst, paint);
		}
	}

	/**
	 * 正向动画效果
	 */
	public void postive() {
		mode = Mode.POSTIVE;
		mAnimator.start();

	}

	/**
	 * 反向动画效果
	 */
	public void negative() {
		mode = Mode.NEGATIVE;
		mAnimator.start();
	}

	@Override
	public void onClick(View arg0) {

		if (mode == mode.NEGATIVE) {
			postive();
		} else {
			negative();
		}
	}
}
