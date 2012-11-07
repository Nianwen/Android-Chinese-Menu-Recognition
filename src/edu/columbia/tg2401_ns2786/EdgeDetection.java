package edu.columbia.tg2401_ns2786;

import java.io.InputStream;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EdgeDetection extends Activity implements View.OnClickListener{

	Intent i;
	Button edgebutton;
	ImageView ivEdgeImage;
	TextView tvTest;
	final static int cameraData = 0;
	Bitmap bmp;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edge_detection);		
		initialiation();
		InputStream is = getResources().openRawResource(R.drawable.ic_launcher);
		bmp = BitmapFactory.decodeStream(is);
	}

	private void initialiation() {
		edgebutton = (Button) findViewById (R.id.edgebutton);
		edgebutton.setOnClickListener(this);
		ivEdgeImage = (ImageView) findViewById (R.id.ivEdgeImage);
		tvTest = (TextView) findViewById (R.id.tvTest);
	}

	@Override
	public void onClick(View arg0) {
		i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(i, cameraData);
	}
	
	public static Bitmap createContrast(Bitmap src, double value) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;
		// get contrast value
		double contrast = Math.pow((100 + value) / 100, 2);

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
		for(int y = 0; y < height; ++y) {
		// get pixel color
		pixel = src.getPixel(x, y);
		A = Color.alpha(pixel);
		// apply filter contrast for every channel R, G, B
		R = Color.red(pixel);
		R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		if(R < 0) { R = 0; }
		else if(R > 255) { R = 255; }

		G = Color.red(pixel);
		G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		if(G < 0) { G = 0; }
		else if(G > 255) { G = 255; }

		B = Color.red(pixel);
		B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		if(B < 0) { B = 0; }
		else if(B > 255) { B = 255; }

		// set new pixel color to output bitmap
		bmOut.setPixel(x, y, Color.argb(A, R, G, B));
		}
		}

		// return final image
		return bmOut;
		}

	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK){
			Bundle extras = data.getExtras();
			
			bmp = (Bitmap) extras.get("data");
			//bmp = bitmap.copy(bitmap.getConfig(), true);

			/*
			Bitmap bmOut = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
			bmOut=createContrast(bmp, 900);
			*/
			
			//choice.setImageBitmap(mutBitmap); 
			Mat mImg = new Mat();
	        mImg = Utils.bitmapToMat(bmp);
	       
	       //Converting to grayscale
	        Mat mGray = new Mat(mImg.rows(), mImg.cols(), CvType.CV_8UC1, new Scalar(0));
	        Imgproc.cvtColor(mImg , mGray, Imgproc.COLOR_BGRA2GRAY, 4); 
	       //Applying Canny
	        Imgproc.Canny(mGray, mGray, 80, 90);
	       
	       //Converting back to 4 channel image
	        Imgproc.cvtColor(mGray , mImg, Imgproc.COLOR_GRAY2RGBA, 4); 
	        bmp.recycle();
	        System.gc();
	        bmp = Bitmap.createBitmap(mImg.cols(), mImg.rows(), Bitmap.Config.ARGB_8888); 
	        Utils.matToBitmap(mImg, bmp);
	        
	        
	        int width = bmp.getWidth();
	        int height = bmp.getHeight();
	        int rs = 0;
	        int ts = 0;
	        
	        int[] sRFlag = new int[10];
	        int[] fRFlag = new int[10];
	        int[] rPFlag = new int[height];
	              
	        int[] whiteCount = new int[height];

	        
	        for (int i = 0; i < height; i++){
	        	for (int j = 0; j < width; j++){
	        		if (bmp.getPixel(j, i) == Color.WHITE){
	        			whiteCount[i]++;

	        			//bmp.setPixel(i, j, Color.RED);
	        		} 
	        	}
	        	if (whiteCount[i] > 12){
	        		rPFlag[i] = 1; //Starting line threshold
	        	}
	        	if (whiteCount[i] > 20){
	        		rPFlag[i] = 10;//Character line threshold
	        	}
	        }
	        
	        
	        for (int i = 5; i < height-5; i++){
	        	if (rs == 0 ){
	        		if ((rPFlag[i] == 10 || rPFlag[i] == 1) && (rPFlag[i-1]< 2) && (rPFlag[i-2] < 2) 
	        				&& (rPFlag[i-3] < 2) && (rPFlag[i-4] < 2) && (rPFlag[i-5] < 2)){
		        		sRFlag[rs] = i;
		        		rs ++;
		        	}
		        	if ((rPFlag[i] == 10 || rPFlag[i] == 1) && (rPFlag[i+1] <2) && (rPFlag[i+2] < 2) && (rPFlag[i+3] < 2)
		        			&& (rPFlag[i+4] < 2) && (rPFlag[i+5] < 2)){
		        		fRFlag[ts] = i;
		        		ts ++;
		        	}
	        	}else if (ts == 0){
	        		if ((rPFlag[i] == 10 || rPFlag[i] == 1) && (rPFlag[i-1] <2) &&(rPFlag[i-2]<2) 
	        				&& (rPFlag[i-3] <2)&& (rPFlag[i-4] < 2) && (rPFlag[i-5] < 2)
	        				&& (i > sRFlag[rs - 1] + 20)){
		        		sRFlag[rs] = i;
		        		rs ++;
		        	}
		        	if ((rPFlag[i] == 10 || rPFlag[i] == 1) && (rPFlag[i+1] == 0 || rPFlag[i+1] == 1) &&
		        			(rPFlag[i+2] < 2) && (rPFlag[i+3] < 2)
		        			&& (rPFlag[i+4] < 2) && (rPFlag[i+5] < 2)){
		        		fRFlag[ts] = i;
		        		ts ++;
		        	}
	        	}else {
	        		if ((rPFlag[i] == 10 || rPFlag[i] == 1) && (rPFlag[i-1] <2) &&(rPFlag[i-2]< 2) 
	        				&& (rPFlag[i-3] <2) && (rPFlag[i-4] < 2) && (rPFlag[i-5] < 2)
		        			&& (i > sRFlag[rs - 1] + 20)){
		        		sRFlag[rs] = i;
		        		rs ++;
		        	}
		        	if ((rPFlag[i] == 10 || rPFlag[i] == 1) && (rPFlag[i+1] == 0 || rPFlag[i+1] == 1) &&
		        			(rPFlag[i+2] == 0 || rPFlag[i+2] == 1) && (rPFlag[i+3] == 0 || rPFlag[i+3] == 1)
		        			&& (rPFlag[i+4] < 2) && (rPFlag[i+5] < 2)
		        			&& (i > fRFlag[ts - 1] + 20)){
		        		fRFlag[ts] = i;
		        		ts ++;
		        	}
	        	}
	        }
	        
	        
	        for (int n = 0; n < 4; n++){
		        int tc = 0;
		        int rc = 0;
		        int[] sCFlag = new int[300];
		        int[] fCFlag = new int[300];
		        int[] cPFlag = new int[width];
	        	
	        	for (int jj = 0; jj < width; jj++){
		        	int cCount = 0;
		        	for (int ii = sRFlag[n]; ii <= fRFlag[n]; ii ++){
			        	if (bmp.getPixel(jj, ii) == Color.WHITE){
			        		cCount ++;
			        	}
			        }	
		        	if (cCount >= 1){
		        		cPFlag[jj] = 1;
		        	}
		        	if (cCount >= 2 ){
		        		cPFlag[jj] = 5;
		        	}
		        	if (cCount >= 6 ){
		        		cPFlag[jj] = 10;
		        	}	        	
	        	}
		        
		        
		        for (int iter = 5; iter < width -5; iter ++){
		        	if (rc == 0){
		        		if ((cPFlag[iter] >= 1 && cPFlag[iter-1]==0&& cPFlag[iter-2]==0&& cPFlag[iter-3]==0) 
			        			|| ((cPFlag[iter] >= 1) && (cPFlag[iter-1] + cPFlag[iter-2] + cPFlag[iter-3] <2))){
			        		sCFlag[rc] = iter;
			        		rc++;
			        	}
			        	if ((cPFlag[iter] >= 1 && cPFlag[iter+1]==0&& cPFlag[iter+2]==0&& cPFlag[iter+3]==0)
			        			|| ((cPFlag[iter] >= 1) && (cPFlag[iter+1] + cPFlag[iter+2] + cPFlag[iter+3] <2))){
			        		fCFlag[tc] = iter;
			        		tc++;
			        	}
		        	}else if (tc == 0){
		        		if ((iter - sCFlag[rc-1] >= fRFlag[n] - sRFlag[n] - 4) && (iter - sCFlag[rc-1] <= (fRFlag[n] - sRFlag[n])*1.5)
		        				&& ((cPFlag[iter] >= 1 && cPFlag[iter-1]==0&& cPFlag[iter-2]==0&& cPFlag[iter-3]==0) 
			        			|| ((cPFlag[iter] >= 1) && (cPFlag[iter-1] + cPFlag[iter-2] + cPFlag[iter-3] <2)))){
			        		sCFlag[rc] = iter;
			        		rc++;
			        	}
			        	if ((cPFlag[iter] >= 1 && cPFlag[iter+1]==0&& cPFlag[iter+2]==0&& cPFlag[iter+3]==0)
			        			|| ((cPFlag[iter] >= 1) && (cPFlag[iter+1] + cPFlag[iter+2] + cPFlag[iter+3] <2))){
			        		fCFlag[tc] = iter;
			        		tc++;
			        	}
		        	}else {
		        		if ((iter - sCFlag[rc-1] >= fRFlag[n] - sRFlag[n] - 4) && (iter - sCFlag[rc-1] <= (fRFlag[n] - sRFlag[n])*1.5)
		        				&& ((cPFlag[iter] >= 1 && cPFlag[iter-1]==0&& cPFlag[iter-2]==0&& cPFlag[iter-3]==0) 
			        			|| ((cPFlag[iter] >= 1) && (cPFlag[iter-1] + cPFlag[iter-2] + cPFlag[iter-3] <2)))){
			        		sCFlag[rc] = iter;
			        		rc++;
			        	}
			        	if ( (iter - fCFlag[rc-1] >= fRFlag[n] - sRFlag[n] - 4) && (iter - fCFlag[rc-1] <= (fRFlag[n] - sRFlag[n])*1.5)
			        			&& (cPFlag[iter] >= 1 && cPFlag[iter+1]==0&& cPFlag[iter+2]==0&& cPFlag[iter+3]==0)
			        			|| ((cPFlag[iter] >= 1) && (cPFlag[iter+1] + cPFlag[iter+2] + cPFlag[iter+3] <2))){
			        		fCFlag[tc] = iter;
			        		tc++;
			        	}
		        	}
		        }
		        
	        	
	        	for (int x = 0; x < rc; x++){
		        	for (int y = sRFlag[n]; y <= fRFlag[n]; y ++){
		        		bmp.setPixel(sCFlag[x],y,Color.YELLOW);
		        		bmp.setPixel(fCFlag[x],y,Color.MAGENTA);
		        	}
		        }	        	
	        }
	        
	        for (int p = 0; p < width; p++){
	        	bmp.setPixel(p,0,Color.BLUE);
	        	bmp.setPixel(p,height-1,Color.BLUE);
	        	bmp.setPixel(p,sRFlag[0]-2,Color.GREEN);
	        	bmp.setPixel(p,sRFlag[1]-2,Color.GREEN);
	        	bmp.setPixel(p,sRFlag[2]-2,Color.GREEN);
	        	bmp.setPixel(p,fRFlag[0]+2,Color.RED);
	        	bmp.setPixel(p,fRFlag[1]+2,Color.RED);
	        	bmp.setPixel(p,fRFlag[2]+2,Color.RED);
	        	if (sRFlag[3] != 0){
		        	bmp.setPixel(p,sRFlag[3]-2,Color.GREEN);	        		
	        	}
	        	if (fRFlag[3] != 0){
		        	bmp.setPixel(p,fRFlag[3]-2,Color.RED);	        		
	        	}
	        	
	        }
	        for (int q = 0; q < height; q++){
	        	bmp.setPixel(0,q,Color.BLUE);
	        	bmp.setPixel(width-1,q,Color.BLUE);
	        }

			ivEdgeImage.setImageBitmap(bmp);
			tvTest.setText("width: " + width + " " + "height: " + height);
		}
	}

}
