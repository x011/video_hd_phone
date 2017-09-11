LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := forcetv
LOCAL_SRC_FILES := libforcetv.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := aplayer
LOCAL_SRC_FILES := libaplayer_android.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := aplayer_ffmpeg
LOCAL_SRC_FILES := libaplayer_ffmpeg.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := vrtoolkit
LOCAL_SRC_FILES := libvrtoolkit.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := stlport_shared
LOCAL_SRC_FILES := libstlport_shared.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := vplayer
LOCAL_SRC_FILES := libvplayer.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := ffmpeg
LOCAL_SRC_FILES := libffmpeg.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := bvvo.9
LOCAL_SRC_FILES := libvvo.9.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := vao.0
LOCAL_SRC_FILES := libvao.0.so
include $(PREBUILT_SHARED_LIBRARY)

