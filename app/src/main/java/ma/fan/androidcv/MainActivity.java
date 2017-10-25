package ma.fan.androidcv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity
{
    OpenCVManager _cvManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());

        _cvManager = new OpenCVManager(this);
        _cvManager.init();
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        _cvManager.enableView();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        _cvManager.disableView();
    }



    @Override
    protected void onPause()
    {
        super.onPause();

        _cvManager.disableView();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
