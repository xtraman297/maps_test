package noam.socialbridge_alfa;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * Created by USER on 16/03/2015.
 */
public class ReturnMarkerWithImage {
    private static SocialBridgeActionsAPI instance;

    public static Bitmap ReturnBitmap(Bitmap UserPicture, Bitmap backgroundMarkerPic){
        Bitmap mutableBitmap = UserPicture.copy(Bitmap.Config.ARGB_8888, true);
        mutableBitmap = Bitmap.createScaledBitmap(mutableBitmap, 300, 300, true);
        backgroundMarkerPic = Bitmap.createScaledBitmap(backgroundMarkerPic, 500, 500, true);
        mutableBitmap = getCroppedBitmap(mutableBitmap);
        mutableBitmap = overlay(backgroundMarkerPic, mutableBitmap);
        return mutableBitmap;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    private static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 96, 46, null);
        return bmOverlay;
    }


}
