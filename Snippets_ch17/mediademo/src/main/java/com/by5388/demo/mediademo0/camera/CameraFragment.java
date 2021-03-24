package com.by5388.demo.mediademo0.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.by5388.demo.mediademo0.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Administrator  on 2020/8/7.
 */
public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    private static final int REQUEST_CODE_CAMERA = 100;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private CameraManager mCameraManager;
    private String[] mCameraIdList = null;
    private String mCameraId;
    private CameraDevice.StateCallback mStateCallback;
    private CameraDevice mCamera;
    private CaptureRequest.Builder mPreviewCaptureRequest;
    private CameraCaptureSession mCaptureSession;
    private Button mButtonTakePicture;
    private Handler mHandler = new Handler();
    private ImageReader mImageReader;
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener;


    public static CameraFragment newInstance() {
        return new CameraFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCameraManager = (CameraManager) Objects.requireNonNull(getContext()).getSystemService(Context.CAMERA_SERVICE);
        if (mCameraManager != null) {
            try {
                mCameraIdList = mCameraManager.getCameraIdList();
                if (mCameraIdList.length > 0) {
                    mCameraId = mCameraIdList[0];
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSurfaceView = view.findViewById(R.id.surface_view_camera);
        mButtonTakePicture = view.findViewById(R.id.button_take_pic);
        mButtonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(createSurfaceHolderCallback());
        //固定大小？
        mHolder.setFixedSize(mSurfaceView.getWidth(), mSurfaceView.getHeight());

        mImageReader = ImageReader.newInstance(1080, 2160,
                ImageFormat.JPEG, 2
        );
        mOnImageAvailableListener = createOnImageAvailableListener();
        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);

        //check_and_request_Permission
        if (grantedCameraPermission()) {
            openCamera();
        } else {
            requestCameraPermission();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // TODO: 2020/8/7
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantedCameraPermission()) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Without Camera Permission!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private SurfaceHolder.Callback createSurfaceHolderCallback() {
        return new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated: ");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startCameraCaptureSession();
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "surfaceCreated: Camera capture failed.", e);
                        }
                    }
                }, 300);


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged: ");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed: ");
            }
        };
    }

    private void startCameraCaptureSession() throws CameraAccessException {
        if (mHolder.isCreating()) {
            // TODO: 2020/8/7 出现了这个异常
            Log.e(TAG, "startCameraCaptureSession: error mHolder.isCreating()");
            return;
        }
        if (mCamera == null) {
            Log.e(TAG, "startCameraCaptureSession: error mCamera == null ");
            return;
        }

        //用于预览
        final Surface previewSurface = mHolder.getSurface();
        mPreviewCaptureRequest = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        //把相机的图像输出到mSurfaceView
        mPreviewCaptureRequest.addTarget(previewSurface);

        final CameraCaptureSession.StateCallback captureSessionCallback = getCaptureSessionCallback();
        try {
            mCamera.createCaptureSession(Collections.singletonList(previewSurface), captureSessionCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "startCameraCaptureSession: Camera Access Exception", e);
        }

    }

    private CameraCaptureSession.StateCallback getCaptureSessionCallback() {
        return new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                mCaptureSession = session;
                //设置重复请求？连续获取？
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewCaptureRequest.build(), null, null);
                } catch (CameraAccessException | IllegalStateException e) {
                    Log.e(TAG, "onConfigured: Capture Session Exception.", e);
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                Log.e(TAG, "onConfigureFailed: Capture Session Configuration Failed.");
            }
        };
    }

    private boolean grantedCameraPermission() {
        return ActivityCompat.checkSelfPermission(
                Objects.requireNonNull(getContext()), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressWarnings("MissingPermission")
    private void openCamera() {
        if (!grantedCameraPermission()) {
            Log.e(TAG, "openCamera: without permission");
            return;
        }
        if (mCameraManager == null) {
            Log.e(TAG, "openCamera: mCameraManager == null");
            return;
        }
        if (TextUtils.isEmpty(mCameraId)) {
            Log.e(TAG, "openCamera: mCameraId == null");
            return;
        }
        try {
            mCameraManager.openCamera(mCameraId, getCameraDeviceStateCallback(), null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "openCamera: Unable to open the camera", e);
        }
    }

    private void requestCameraPermission() {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.e(TAG, "requestCameraPermission: activity == null");
            return;
        }
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
    }

    private CameraDevice.StateCallback getCameraDeviceStateCallback() {
        if (mStateCallback == null) {
            mStateCallback = new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    Log.d(TAG, "onOpened: ");
                    mCamera = camera;
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    Log.d(TAG, "onDisconnected: ");
                    camera.close();
                    mCamera = null;

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    mCamera = null;
                    Log.e(TAG, "onError: error = " + error);

                }
            };
        }
        return mStateCallback;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.close();
            mCamera = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (grantedCameraPermission() && mCamera != null) {
            openCamera();
        }
    }

    /**
     * 拍照，预览
     */
    private void takePicture() {
        if (mCamera == null) {
            return;
        }
        try {
            final CaptureRequest.Builder captureRequest = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequest.addTarget(mImageReader.getSurface());
            mCaptureSession.capture(captureRequest.build(), null, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "takePicture: ", e);
            e.printStackTrace();
        }
    }

    private ImageReader.OnImageAvailableListener createOnImageAvailableListener() {
        return new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                if (reader == null) {
                    return;
                }

                try (final Image image = reader.acquireNextImage()) {
                    if (image == null) {
                        return;
                    }
                    final Image.Plane[] planes = image.getPlanes();
                    if (planes != null && planes.length > 0) {
                        final ByteBuffer buffer = planes[0].getBuffer();
                        final byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        saveImage(data);
                    }

                }


            }
        };
    }

    private void saveImage(byte[] data) {
        if (data == null) {
            return;
        }
        final long time = System.currentTimeMillis();
        try {
            final File file = new File(ContextCompat.getDataDir(getContext()), time + ".jpg");
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data);
            fileOutputStream.close();
            Log.d(TAG, "saveImage: 保存图片成功");
        } catch (IOException e) {
            Log.e(TAG, "saveImage: ", e);
            e.printStackTrace();
        }

    }
}
