package ma.fan.androidcv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    private CVRunner _cvManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _cvManager = new CVRunner(this);
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


    @Override
    protected  void onStop()
    {
        super.onStop();

        _cvManager.disableView();
    }
}
