package com.zhouyou.gesture.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：ZhouYou
 * 日期：2016/9/3.
 * 贴纸的使用控件
 */
public class StickerLayout extends FrameLayout {

    private Context context;
    // 贴纸的集合
    private List<StickerView> stickerViews;
    // 贴纸的View参数
    private FrameLayout.LayoutParams stickerParams;
    // 背景图片控件
    private ImageView ivImage;
    // 背景图片的位图
    private Bitmap bgBitmap;

    public StickerLayout(Context context) {
        this(context, null);
    }

    public StickerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        stickerViews = new ArrayList<>();
        stickerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        addBackgroundImage();
    }

    /**
     * 初始化背景图片控件
     */
    private void addBackgroundImage() {
        FrameLayout.LayoutParams bgParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        ivImage = new ImageView(context);
        ivImage.setScaleType(ImageView.ScaleType.FIT_XY);
        ivImage.setLayoutParams(bgParams);
        addView(ivImage);
    }

    /**
     * 设置背景图片
     */
    public void setBackgroundImage(int resource) {
        bgBitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        ivImage.setImageResource(resource);
    }

    public void setBackgroundImage(Bitmap bitmap) {
        this.bgBitmap = bitmap;
        ivImage.setImageBitmap(bitmap);
    }

    public void setBackgroundURI(URI uri) {

    }

    /**
     * 新增贴纸
     */
    public void addSticker(int resource) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        addSticker(bitmap);
    }

    /**
     * 新增贴纸
     */
    public void addSticker(Bitmap bitmap) {
        final StickerView sv = new StickerView(context);
        sv.setImageBitmap(bitmap);
        sv.setEdit(true);
        sv.setLayoutParams(stickerParams);
        sv.setOnStickerActionListener(new StickerView.OnStickerActionListener() {
            @Override
            public void onDelete() {
                // 处理删除操作
                removeView(sv);
                stickerViews.remove(sv);
                reset();
            }

            @Override
            public void onEdit(StickerView stickerView) {
                int position = stickerViews.indexOf(stickerView);
                stickerView.setEdit(true);
                stickerView.bringToFront();

                int size = stickerViews.size();
                for (int i = 0; i < size; i++) {
                    StickerView item = stickerViews.get(i);
                    if (item == null) continue;
                    if (position != i) {
                        item.setEdit(false);
                    }
                }
            }
        });
        addView(sv);
        stickerViews.add(sv);
    }

    /**
     * 重置贴纸的操作列表
     */
    private void reset() {
        int size = stickerViews.size();
        if (size <= 0) return;
        for (int i = size - 1; i >= 0; i--) {
            StickerView item = stickerViews.get(i);
            if (item == null) continue;
            if (i == size - 1) {
                item.setEdit(true);
            } else {
                item.setEdit(false);
            }
            stickerViews.set(i, item);
        }
    }

    /**
     * 生成合成图片
     *
     * @return
     */
    public Bitmap generateCombinedBitmap() {
        Bitmap dstBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        // 画背景
        canvas.drawBitmap(bgBitmap, 0, 0, null);
        // 画水印
        for (StickerView view : stickerViews) {
            if (view == null
                    || view.getSticker() == null
                    || view.getSticker().getSrcImage() == null
                    || view.getSticker().getMatrix() == null) continue;
            canvas.drawBitmap(view.getSticker().getSrcImage(), view.getSticker().getMatrix(), null);
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        // 根据最终的bitmap在本地生成路径
        return dstBitmap;
    }
}
