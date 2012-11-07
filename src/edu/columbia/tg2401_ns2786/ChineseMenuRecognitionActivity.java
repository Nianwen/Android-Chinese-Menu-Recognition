package edu.columbia.tg2401_ns2786;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import edu.columbia.tg2401_ns2786.R;
import edu.columbia.tg2401_ns2786.ChineseMenuRecognitionActivity;
import edu.columbia.tg2401_ns2786.ChineseMenuRecognitionActivity.ButtonClickHandler;
import com.googlecode.tesseract.android.TessBaseAPI;


public class ChineseMenuRecognitionActivity extends Activity {
    /** Called when the activity is first created. */
	public static final String PACKAGE_NAME = "edu.columbia.tg2401_ns2786";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/AndroidOCR/";
	
	public static final String lang = "chi_sim";

	private static final String TAG = "ChineseMenuRecognition.java";

	protected ImageView ivReturnedImg;
	protected Button _button;
	// protected ImageView _image;
	protected TextView _field;
	protected String _path;
	protected boolean _taken;

	protected static final String PHOTO_TAKEN = "photo_taken";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/chi_sim.traineddata");

				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/chi_sim.traineddata");

				byte[] buf = new byte[1024];
				int len;
				
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();				
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}	
		  	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();
    }
    
	private void initialize(){
		ivReturnedImg = (ImageView) findViewById (R.id.vReturnedPic);
		_field = (TextView) findViewById(R.id.field);
		_button = (Button) findViewById(R.id.button);
		_button.setOnClickListener(new ButtonClickHandler());
		// _image = (ImageView) findViewById(R.id.image);
		_path = DATA_PATH + "/ocr.jpg";
	}
	
	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.v(TAG, "Starting Camera app");
			startCameraActivity();
		}
	}
	
	protected void startCameraActivity() {
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG, "resultCode: " + resultCode);

		if (resultCode == -1) {
			onPhotoTaken();
		} else {
			Log.v(TAG, "User cancelled");
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(ChineseMenuRecognitionActivity.PHOTO_TAKEN, _taken);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(ChineseMenuRecognitionActivity.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}
	
	protected void onPhotoTaken() {
		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

		try {
			ExifInterface exif = new ExifInterface(_path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;

			
			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}
			

			Log.v(TAG, "Rotation: " + rotate);

			if (rotate != 0) {

				// Getting width & height of the given image.
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}

			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		// _image.setImageBitmap( bitmap );
		
		Log.v(TAG, "Before baseApi");
		
		
		//choice.setImageBitmap(mutBitmap); 
		Mat mImg = new Mat();
        mImg = Utils.bitmapToMat(bitmap);
       
       //Converting to grayscale
        Mat mGray = new Mat(mImg.rows(), mImg.cols(), CvType.CV_8UC1, new Scalar(0));
        Imgproc.cvtColor(mImg , mGray, Imgproc.COLOR_BGRA2GRAY, 4); 
       
       //Converting back to 4 channel image
        Imgproc.cvtColor(mGray , mImg, Imgproc.COLOR_GRAY2RGBA, 4); 
        bitmap.recycle();
        System.gc();
        bitmap = Bitmap.createBitmap(mImg.cols(), mImg.rows(), Bitmap.Config.ARGB_8888); 
        Utils.matToBitmap(mImg, bitmap);

        
        
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);
		String recognizedText1 = baseApi.getUTF8Text();
		String recognizedText2 = baseApi.getUTF8Text();
		String recognizedText = recognizedText1+recognizedText2; 
		baseApi.end();

		Log.v(TAG, "OCRED TEXT: " + recognizedText);

		if ( lang.equalsIgnoreCase("eng") ) {
			recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
		}
		
		recognizedText = recognizedText.replaceAll(" ", "");
		recognizedText = recognizedText.replaceAll("[[a-z][A-Z][0-9]]", "");
		
		recognizedText = recognizedText.trim();
		int lenghtOfIt = recognizedText.indexOf("\n");
		if (lenghtOfIt == -1){
			lenghtOfIt = recognizedText.length() / 2;
		}
		
		String recognizedTextFirstLine = recognizedText.substring(0, lenghtOfIt);
		String dishName = translatedOutput(recognizedTextFirstLine);
		_field.setText(dishName + "\n" + recognizedTextFirstLine);
		
		if ( dishName == "Twice-cooked pork"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.huiguorou);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "Yu-Shiang shredded pork"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.yuxiangrousi);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "Sauteed chicken dices with chili peppers"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.lazijiding);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "pork with vegetables"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.muxurou);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "Spicy pork"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.xianglarousi);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "Stewed dishes"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.dahuicai);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "Xinjiang chicken"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.xijiangdapanji);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "Pork with pepper"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.jianjiaorousi);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "Spicy chicken"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.xianglajikuai);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "Sweet and sour fillet of pork"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.tangculiji);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "beef with potatos"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.tudoushaoniurou);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "Boiled pork slices"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.shuizhuroupian);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "Braised pork with vermicelli"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.zhuroudunfentiao);
			ivReturnedImg.setImageURI(path);
		}else if (dishName == "Sauteed shredded pork in sweet bean sauce"){
			Uri path = Uri.parse("android.resource://edu.columbia.tg2401_ns2786/"+R.drawable.jianjiaorousi);
			ivReturnedImg.setImageURI(path);
		}
		
	}
	
	
	
	public String translatedOutput ( String str ){
		String dishName = "Ooops";
		String rawMaterial = "I dont know";
		String cookedWay = "I dont know";
		String flavor = "I dont know";
		String dishInfo;
		if (str.indexOf("回锅肉") != -1){
			dishName = "Twice-cooked pork";
		}else if (str.indexOf("鱼香肉丝") != -1){
			dishName = "Yu-Shiang shredded pork";
		}else if (str.indexOf("辣子鸡丁") != -1){
			dishName = "Sauteed chicken dices with chili peppers";
		}else if (str.indexOf("木须肉") != -1){
			dishName = "pork with vegetables";
		}else if (str.indexOf("香辣肉丝") != -1){
			dishName = "Spicy pork";
		}else if (str.indexOf("大烩菜") != -1){
			dishName = "Stewed dishes";
		}else if (str.indexOf("新疆大盘鸡") != -1){
			dishName = "Xinjiang chicken";
		}else if (str.indexOf("尖椒肉丝") != -1){
			dishName = "Pork with pepper";
		}else if (str.indexOf("香辣鸡块") != -1){
			dishName = "Spicy chicken";
		}else if (str.indexOf("糖醋里脊") != -1){
			dishName = "Sweet and sour fillet of pork";
		}else if (str.indexOf("土豆烧牛肉") != -1){
			dishName = "beef with potatos";
		}else if (str.indexOf("水煮肉片") != -1){
			dishName = "Boiled pork slices";
		}else if (str.indexOf("猪肉炖粉条") != -1){
			dishName = "Braised pork with vermicelli";
		}else if (str.indexOf("京酱肉丝") != -1){
			dishName = "Sauteed shredded pork in sweet bean sauce";
		}

		if (dishName == "Ooops"){
			if (str.indexOf("肉") != -1){
				rawMaterial = "pork";
			}else if (str.indexOf("鸡") != -1){
				rawMaterial = "chicken";
			}else if (str.indexOf("鸭") != -1){
				rawMaterial = "duck";
			}else if (str.indexOf("鱼") != -1){
				rawMaterial = "fish";
			}else if (str.indexOf("牛") != -1){
				rawMaterial = "beef";
			}else if (str.indexOf("猪") != -1){
				rawMaterial = "pork";
			}else if (str.indexOf("肉") != -1){
				rawMaterial = "pork";
			}else if (str.indexOf("里脊") != -1){
				rawMaterial = "pork";
			}else if (str.indexOf("椒") != -1){
				rawMaterial = "pepper";
			}
		}
		
		if(str.indexOf("炸") !=- 1){
			cookedWay="fried";
		}else if (str.indexOf("煎") != -1){
			flavor = "fried";
		}else if (str.indexOf("蒸") != -1){
			flavor = "steamed";
		}
		
		if(str.indexOf("糖") !=- 1){
			flavor ="sweet";
		}else if (str.indexOf("辣") != -1){
			flavor = "spicy";
		}else if (str.indexOf("酸") != -1){
			flavor = "sour";
		}else if (str.indexOf("甜") != -1){
			flavor = "sweet";
		}else if (str.indexOf("苦") != -1){
			flavor = "bitter";
		}
		/*--------output logic----------*/
		if (dishName != "Ooops"){
			return dishName;			
		}else {
			if (rawMaterial != "I dont know" && cookedWay != "I dont know" && flavor != "I dont know"){
				dishInfo = "This dish contains raw matiral: " + rawMaterial + "\n" +
						   "This dish is " + cookedWay + " and " + flavor + "\n";					   
			}else if (rawMaterial != "I dont know" && cookedWay != "I dont know"){
				dishInfo = "This dish contains raw matiral: " + rawMaterial + "\n" +
						   "This dish is " + cookedWay + "\n";
			}else if (rawMaterial != "I dont know" && flavor != "I dont know"){
				dishInfo = "This dish contains raw matiral: " + rawMaterial + "\n" +
						   "This dish is " + flavor + "\n";
			}else if (cookedWay != "I dont know" && flavor != "I dont know"){
				dishInfo = "This dish is " + cookedWay + " and " + flavor + "\n";					   
			}else if (rawMaterial != "I dont know" ){
				dishInfo = "This dish contains raw matiral: " + rawMaterial + "\n";					   
			}else if (cookedWay != "I dont know"){
				dishInfo = "This dish is " + cookedWay + "\n";					   
			}else if (flavor != "I dont know"){
				dishInfo = "This dish is " + flavor + "\n";					   
			}else {
				dishInfo = "Sorry, we don't have the info of this dish. \n";
			}
			return (dishInfo);
		}
	}
	
}