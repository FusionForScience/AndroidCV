package ma.fan.androidcv;


import android.app.Activity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;


/**
 *
 */
public class OpenCVManager implements CameraBridgeViewBase.CvCameraViewListener2
{
    private Activity _mainActivity;                 // Main activity of the app

    private JavaCameraView _javaCameraView;         // Camera view in the robot controller

    ArrayList<Mat> channels = new ArrayList<>();

    // Mat values
    private Mat _rgba;
    Mat ycrcb;
    Mat result;

    // Loader callback, for when activity state changes
    private BaseLoaderCallback _loaderCallBack;


    /**
     * Constructor- takes the object holding the main activity
     *
     * @param MAIN_ACTIVITY The main activity of the app
     */
    public OpenCVManager(final Activity MAIN_ACTIVITY)
    {
        _mainActivity = MAIN_ACTIVITY;

        _loaderCallBack = new BaseLoaderCallback(_mainActivity)
        {
            @Override
            public void onManagerConnected(int status)
            {
                switch(status)
                {
                    case BaseLoaderCallback.SUCCESS:
                        _javaCameraView.enableView();
                        break;

                    default:
                        super.onManagerConnected(status);
                        break;
                }

                super.onManagerConnected(status);
            }
        };
    }


    @Override
    public void onCameraViewStarted(int width, int height)
    {
        _rgba = new Mat(height , width , CvType.CV_8UC4);
        ycrcb = new Mat(height , width , CvType.CV_8UC4);
        result = new Mat(height , width , CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped()
    {
        _rgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
//        _rgba = inputFrame.rgba();
        inputFrame.rgba().copyTo(_rgba);

        if(_rgba.channels() >= 3)
        {
            Imgproc.cvtColor(_rgba , ycrcb , Imgproc.COLOR_RGB2YCrCb);
            Core.split(ycrcb , channels);
            Imgproc.equalizeHist(channels.get(0) , channels.get(0));
            Core.merge(channels , ycrcb);
            Imgproc.cvtColor(ycrcb , result , Imgproc.COLOR_YCrCb2RGB);

            ycrcb.release();
            _rgba.release();

            System.gc();
//            System.runFinalization();

            return result;
        }

        _rgba.release();

        return new Mat();
    }


    public void init()
    {
        if (!OpenCVLoader.initDebug())
        {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        }
        else
        {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

        _javaCameraView = _mainActivity.findViewById(R.id.java_camera_view);
        _javaCameraView.setVisibility(SurfaceView.VISIBLE);
        _javaCameraView.setCvCameraViewListener(this);
    }


    public void enableView()
    {
        if (!OpenCVLoader.initDebug())
        {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0 , _mainActivity ,
                    _loaderCallBack);
        }
        else
        {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
            _loaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    public void disableView()
    {
        if(_javaCameraView != null)
        {
            _javaCameraView.disableView();
        }
    }


    public void enableCameraView()
    {
        _javaCameraView.setVisibility(View.VISIBLE);
    }


    public void disableCameraView()
    {
        _javaCameraView.setVisibility(View.GONE);
    }
}
