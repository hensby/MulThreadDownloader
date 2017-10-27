package com.android.downloader;

import java.io.File;

import com.android.network.DownloadProgressListener;
import com.android.network.FileDownloader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText downloadpathText;
    private TextView resultView;
    private ProgressBar progressBar;
    

    private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {			
			switch (msg.what) {
			case 1:				
				progressBar.setProgress(msg.getData().getInt("size"));
				float num = (float)progressBar.getProgress()/(float)progressBar.getMax();
				int result = (int)(num*100);
				resultView.setText(result+ "%");
				

				if(progressBar.getProgress()==progressBar.getMax()){
					Toast.makeText(MainActivity.this, R.string.success, 1).show();
				}
				break;
			case -1:

				Toast.makeText(MainActivity.this, R.string.error, 1).show();
				break;
			}
		}
    };
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        downloadpathText = (EditText) this.findViewById(R.id.path);
        progressBar = (ProgressBar) this.findViewById(R.id.downloadbar);
        resultView = (TextView) this.findViewById(R.id.resultView);
        Button button = (Button) this.findViewById(R.id.button);
        
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String path = downloadpathText.getText().toString();
				System.out.println(Environment.getExternalStorageState()+"------"+Environment.MEDIA_MOUNTED);
				
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

					download(path, Environment.getExternalStorageDirectory());
				}else{

					Toast.makeText(MainActivity.this, R.string.sdcarderror, 1).show();
				}
			}
		});
    }
    

    private void download(final String path, final File savedir) {
    	new Thread(new Runnable() {			
			@Override
			public void run() {

				FileDownloader loader = new FileDownloader(MainActivity.this, path, savedir, 3);
		    	progressBar.setMax(loader.getFileSize());
		    	
				try {
					loader.download(new DownloadProgressListener() {
						@Override
						public void onDownloadSize(int size) {
							Message msg = new Message();
							msg.what = 1;
							msg.getData().putInt("size", size);
							handler.sendMessage(msg);
						}
					});
				} catch (Exception e) {
					handler.obtainMessage(-1).sendToTarget();
				}
			}
		}).start();
	}
}