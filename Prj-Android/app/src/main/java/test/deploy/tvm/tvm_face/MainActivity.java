package test.deploy.tvm.tvm_face;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import recognition.VehicleRecognition;

public class MainActivity extends Activity implements AlertDialog.OnClickListener,View.OnClickListener{

    // Used to load the 'native-lib' library on application startup.

    public Button btn;
    public Button recogBtn;
    public TextView resbox;
    public TextView runtimebox;

    public ImageView imgv;
    public SeekBar sb;

    private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
    private static final int REQUEST_CODE_IMAGE_OP = 2;
    private static final int REQUEST_CODE_OP = 3;
    private Uri mPath;
    VehicleRecognition vehicleRecognition;

    Bitmap latestBitmap;
    public TextView tv;


//    public  PlateRecognition pr;

    public long handle;
    private final String TAG = this.getClass().toString();


    public void copyFilesFromAssets(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                // directory
                File file = new File(newPath);
                if (!file.mkdir())
                {
                    Log.d("mkdir","can't make folder");

                }
//                    return false;                // copy recursively
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context, oldPath + "/" + fileName,
                            newPath + "/" + fileName);
                }
            } else {
                // file
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public void initRecognizer()
    {

    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }




    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI,
                new String[]{MediaStore.Images.ImageColumns.DATA},//
                null, null, null);
        if (cursor == null) result = contentURI.getPath();
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }


    /**
     * @param uri
     * @return
     */
    private String getPath(Uri uri) {

    //    return getRealPathFromURI(MainActivity.this,uri);

        //return  uri.getPath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(this, uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    return null;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    return null;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(this, contentUri, selection, selectionArgs);
            }
        }
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor actualimagecursor = managedQuery(uri, proj,null,null,null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        String end = img_path.substring(img_path.length() - 4);
        if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
            return null;
        }
        return img_path;
    }

    public static Bitmap decodeImage(String path) {
        Bitmap res;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inSampleSize = 1;
            op.inJustDecodeBounds = false;
            //op.inMutable = true;
            res = BitmapFactory.decodeFile(path, op);
            //rotate and scale.
            Matrix matrix = new Matrix();

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }

            Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
            Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

            if (!temp.equals(res)) {
                res.recycle();
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
            mPath = data.getData();
            String file = getPath(mPath);
            Bitmap bmp = decodeImage(file);
            if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0 ) {
                Log.e(TAG, "error");
            } else {
                Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
            }
            latestBitmap = bmp;
            imgv.setImageBitmap(latestBitmap);
            //SimpleRecog(bmp,sb.getProgress());
//            startOilPainting(bmp, file);
        } else if (requestCode == REQUEST_CODE_OP) {
            Log.i(TAG, "RESULT =" + resultCode);
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            String path = bundle.getString("imagePath");
            Log.i(TAG, "path="+path);
        } else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            String file = getPath(mPath);
            Bitmap bmp = decodeImage(file);
            Log.d("bmp",bmp.getHeight()+"");

            latestBitmap = bmp;
            imgv.setImageBitmap(latestBitmap);
            //SimpleRecog(bmp,sb.getProgress());
//            startOilPainting(bmp, file);
        }

    }

    String InitModelFiles()
    {
        String assetPath = "vehicle_models";
        String sdcardPath = Environment.getExternalStorageDirectory()
                + File.separator + assetPath;
        copyFilesFromAssets(this, assetPath, sdcardPath);
        Log.d("sdcard",sdcardPath);

        return sdcardPath;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String res = InitModelFiles();
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    {permission},123);

        }
        btn = (Button)findViewById(R.id.select1);
        recogBtn = (Button)findViewById(R.id.reg);
        imgv = (ImageView)findViewById(R.id.imageView2);

        tv = (TextView) findViewById(R.id.team);


        vehicleRecognition = new VehicleRecognition("/sdcard/vehicle_models");

        initRecognizer();
        recogBtn.setOnClickListener(this);
        btn.setOnClickListener(this);

         //Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id);

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which){
            case 1:
                Intent getImageByCamera = new Intent(
                        "android.media.action.IMAGE_CAPTURE");
                ContentValues values = new ContentValues(1);

                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                mPath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, mPath);

                startActivityForResult(getImageByCamera, REQUEST_CODE_IMAGE_CAMERA);
                break;
            case 0:
                Intent getImageByalbum = new Intent(Intent.ACTION_GET_CONTENT);
                getImageByalbum.addCategory(Intent.CATEGORY_OPENABLE);
                getImageByalbum.setType("image/jpeg");
                startActivityForResult(getImageByalbum, REQUEST_CODE_IMAGE_OP);
                break;
            default:;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.select1:
                new AlertDialog.Builder(this)
                        .setTitle("打开方式")
                        .setItems(new String[]{"打开图片"}, this)
                        .show();
                break;
            case R.id.reg:
                String std =  vehicleRecognition.ZeuseesFaceDection(latestBitmap);
                tv.setText(std);
                break;
        }
    }
}

