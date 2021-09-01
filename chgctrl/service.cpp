/*
 * Copyright (C) 2022 Project Kaleidoscope
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "charge-control-hal"

#include <android-base/logging.h>
#include <android-base/properties.h>
#include <binder/ProcessState.h>
#include <hidl/HidlTransportSupport.h>
#include "ChargeControl.h"

using vendor::kscope::chgctrl::V1_0::IChargeControl;
using vendor::kscope::chgctrl::implementation::ChargeControl;

int main() {
    android::sp<IChargeControl> service = new ChargeControl();

    android::hardware::configureRpcThreadpool(1, true /*callerWillJoin*/);

    if (service->registerAsService() != android::OK) {
        LOG(ERROR) << "Cannot register chgctrl HAL service.";
        return 1;
    }

    LOG(INFO) << "chgctrl HAL service ready.";

    android::hardware::joinRpcThreadpool();

    LOG(ERROR) << "chgctrl HAL service failed to join thread pool.";
    return 1;
}
