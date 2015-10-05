package jhhy.co.kr.taclunchmenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.loopj.android.image.SmartImageView;

/**
 * Created by jhkim on 2015-05-06.
 */
public class HwiSmartImageView extends SmartImageView
{
    private OnImageChangeListener imgChangeListener;
    public HwiSmartImageView(Context context)
    {
        super(context);
    }

    public HwiSmartImageView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    public HwiSmartImageView(Context context, AttributeSet attributeSet, int i)
    {
        super(context, attributeSet, i);
    }

    public void setOnImageChangeListener(OnImageChangeListener listener)
    {
        this.imgChangeListener = listener;
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);
        if(this.imgChangeListener != null)
        this.imgChangeListener.onChangeImage(HwiSmartImageView.this);
    }

    public interface OnImageChangeListener
    {
        public void onChangeImage(HwiSmartImageView hwiv);
    }
}
