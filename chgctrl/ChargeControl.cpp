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

#include "ChargeControl.h"
#include <android-base/logging.h>
#include <fcntl.h>

#define CHARGE_CONTROL_NODE "/sys/class/power_supply/battery/op_disable_charge"
#define CHARGE_ENABLED '0'
#define CHARGE_DISABLED '1'

namespace vendor::kscope::chgctrl::implementation {

static int readNode(char& ret) {
    int fd;
    int err = 0;

    fd = open(CHARGE_CONTROL_NODE, O_RDONLY);
    if (fd < 0)
        return 1;

    err = read(fd, &ret, sizeof(char));
    close(fd);

    if (err < 0)
        return 1;

    return 0;
}

static int writeNode(char value) {
    int fd;
    int err = 0;

    fd = open(CHARGE_CONTROL_NODE, O_WRONLY);
    if (fd < 0)
        return 1;

    err = write(fd, &value, sizeof(char));
    close(fd);

    if (err < 0)
        return 1;

    return 0;
}

// Methods from ::vendor::kscope::chgctrl::V1_0::IChargeControl follow.
Return<bool> ChargeControl::getChargeEnabled() {
    char status = 0;

    if (readNode(status))
        LOG(ERROR) << "Unable to get status";

    if (status == CHARGE_DISABLED)
        return false;

    return true;
}

// Return true on failure
Return<bool> ChargeControl::setChargeEnabled(bool enabled) {
    char value;

    if (enabled)
        value = CHARGE_ENABLED;
    else
        value = CHARGE_DISABLED;

    return writeNode(value);
}


// Methods from ::android::hidl::base::V1_0::IBase follow.

//IChargeControl* HIDL_FETCH_IChargeControl(const char* /* name */) {
    //return new ChargeControl();
//}
//
}  // namespace vendor::kscope::chgctrl::implementation
