#
# Copyright (C) 2018 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_PACKAGE_NAME := DeviceSettings

LOCAL_CERTIFICATE := platform
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_PRIVILEGED_MODULE := true
LOCAL_SYSTEM_EXT_MODULE := true
LOCAL_MODULE_TAGS := optional

LOCAL_USE_AAPT2 := true

LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.core_core \
    androidx.preference_preference

LOCAL_STATIC_JAVA_LIBRARIES := \
    org.lineageos.platform.internal

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res

package_resource_overlays := $(strip \
    $(wildcard $(foreach dir, $(PRODUCT_PACKAGE_OVERLAYS), \
      $(addprefix $(dir)/, packages/apps/DeviceSettings/res))) \
    $(wildcard $(foreach dir, $(DEVICE_PACKAGE_OVERLAYS), \
      $(addprefix $(dir)/, packages/apps/DeviceSettings/res))))

LOCAL_RESOURCE_DIR := $(package_resource_overlays) $(LOCAL_RESOURCE_DIR)
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include frameworks/base/packages/SettingsLib/common.mk
include $(BUILD_PACKAGE)
