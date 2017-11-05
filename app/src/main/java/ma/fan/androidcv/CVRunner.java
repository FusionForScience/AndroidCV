package ma.fan.androidcv;


import android.app.Activity;
import android.util.Log;
import android.view.SurfaceView;

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
 * Runs an instance of OpenCV, does some light image processing
 */
public class CVRunner implements CameraBridgeViewBase.CvCameraViewListener2
{
    private Activity _mainActivity;                 // Main activity of the app

    private JavaCameraView _javaCameraView;         // Camera view in the robot controller

    // Mat values
    private Mat _rgba;
    private Mat _ycrcb;
    private Mat _result;

    // Loader callback, for when activity state changes
    private BaseLoaderCallback _loaderCallBack;


    /**
     * Constructor- takes the object holding the main activity
     *
     * @param MAIN_ACTIVITY The main activity of the app
     */
    CVRunner(final Activity MAIN_ACTIVITY)
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


    /**
     * Initializes Mats for use
     *
     * @param width -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    @Override
    public void onCameraViewStarted(int width, int height)
    {
        _rgba = new Mat(height , width , CvType.CV_8UC4);
        _ycrcb = new Mat(height , width , CvType.CV_8UC4);
        _result = new Mat(height , width , CvType.CV_8UC4);
    }


    /**
     * Releases all the Mats
     */
    @Override
    public void onCameraViewStopped()
    {
        _rgba.release();
        _ycrcb.release();
        _result.release();
    }


    /**
     * Takes a camera frame, does some processing, and then returns in to the JavaCameraFrame
     *
     * @param inputFrame Frame to be processed
     *
     * @return Processed frame
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        ArrayList<Mat> channels = new ArrayList<>();

        inputFrame.rgba().copyTo(_rgba);

        if(_rgba.channels() >= 3)
        {
            Imgproc.cvtColor(_rgba , _ycrcb, Imgproc.COLOR_RGB2YCrCb);
            Core.split(_ycrcb, channels);
            Imgproc.equalizeHist(channels.get(0) , channels.get(0));
            Core.merge(channels , _ycrcb);
            Imgproc.cvtColor(_ycrcb, _result, Imgproc.COLOR_YCrCb2RGB);

            _ycrcb.release();
            _rgba.release();

            System.gc();
            System.runFinalization();

            return _result;
        }

        _rgba.release();

        return new Mat();
    }


    /**
     * Initializes OpenCV for use- call this in onCreate()
     */
    void init()
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


    /**
     * Enables OpenCV. Call this in:
     *
     * onResume()
     */
    void enableView()
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


    /**
     * Disables OpenCV. Call this in:
     *
     * onDestroy()
     * onPause()
     * onStop()
     */
    void disableView()
    {
        if(_javaCameraView != null)
        {
            _javaCameraView.disableView();
        }
    }
}
