/*
 * Copyright (C) 2018 The LineageOS Project
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

#define LOG_TAG "tri-state-key_daemon"

#include <android-base/file.h>
#include <android-base/logging.h>
#include <fcntl.h>
#include <linux/input.h>
#include <linux/uinput.h>
#include <unistd.h>

#include "uevent_listener.h"

#define HALL_CALIBRATION_DATA "/sys/devices/platform/soc/soc:tri_state_key/hall_data_calib"
#define HALL_PERSIST_CALIBRATION_DATA "/mnt/vendor/persist/engineermode/tri_state_hall_data"

#define KEY_MODE_NORMAL 601
#define KEY_MODE_VIBRATION 602
#define KEY_MODE_SILENCE 603

using android::base::ReadFileToString;
using android::base::WriteStringToFile;
using android::Uevent;
using android::UeventListener;

static int getUinputFd(int& uinputFd) {
    struct uinput_user_dev uidev {};
    int err;

    uinputFd = open("/dev/uinput", O_WRONLY | O_NONBLOCK);
    if (uinputFd < 0)
        return 1;

    err = ioctl(uinputFd, UI_SET_EVBIT, EV_KEY) |
          ioctl(uinputFd, UI_SET_KEYBIT, KEY_MODE_NORMAL) |
          ioctl(uinputFd, UI_SET_KEYBIT, KEY_MODE_VIBRATION) |
          ioctl(uinputFd, UI_SET_KEYBIT, KEY_MODE_SILENCE);
    if (err != 0)
        goto fail;

    sprintf(uidev.name, "uinput-tri-state-key");
    uidev.id.bustype = BUS_VIRTUAL;

    err = write(uinputFd, &uidev, sizeof(uidev));
    if (err < 0)
        goto fail;

    err = ioctl(uinputFd, UI_DEV_CREATE);
    if (err < 0)
        goto fail;

    return 0;
fail:
    close(uinputFd);
    return 1;
}

static int reportKey(int keyCode, int& uinputFd) {
    int err;
    struct input_event event {};

    event.type = EV_KEY;
    event.code = keyCode;
    event.value = 1;
    err = write(uinputFd, &event, sizeof(event));
    if (err < 0)
        return 1;

    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
    err = write(uinputFd, &event, sizeof(event));
    if (err < 0)
        return 1;

    event.type = EV_KEY;
    event.code = keyCode;
    event.value = 0;
    err = write(uinputFd, &event, sizeof(event));
    if (err < 0)
        return 1;

    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
    err = write(uinputFd, &event, sizeof(event));
    if (err < 0)
        return 1;

    return 0;
}

int main() {
    int uinputFd;
    UeventListener uevent_listener;

    if (std::string hallData; ReadFileToString(HALL_PERSIST_CALIBRATION_DATA, &hallData)) {
        std::replace(hallData.begin(), hallData.end(), ';', ',');
        WriteStringToFile(hallData, HALL_CALIBRATION_DATA);
    }

    if (getUinputFd(uinputFd)) {
        LOG(ERROR) << "Unable to set up uinput fd";
        return 1;
    }

    uevent_listener.Poll([&uinputFd](const Uevent& uevent) {
        if (uevent.action != "change" || uevent.name != "soc:tri_state_key")
            return;

        bool none = uevent.state.find("USB=0") != std::string::npos;
        bool vibration = uevent.state.find("USB-HOST=0") != std::string::npos;
        bool silent = uevent.state.find("null)=0") != std::string::npos;
        int err = 0;

        if (none && !vibration && !silent)
            err = reportKey(KEY_MODE_NORMAL, uinputFd);
        else if (!none && vibration && !silent)
            err = reportKey(KEY_MODE_VIBRATION, uinputFd);
        else if (!none && !vibration && silent)
            err = reportKey(KEY_MODE_SILENCE, uinputFd);

        if (err)
            LOG(ERROR) << "Unable to report key event";
    });

    // The loop can only be exited via failure or signal
    return 1;
}
