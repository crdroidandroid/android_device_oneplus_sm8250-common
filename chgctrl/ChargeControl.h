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

#pragma once

#include <vendor/kscope/chgctrl/1.0/IChargeControl.h>
#include <hidl/MQDescriptor.h>
#include <hidl/Status.h>

namespace vendor::kscope::chgctrl::implementation {

using ::android::hardware::hidl_array;
using ::android::hardware::hidl_memory;
using ::android::hardware::hidl_string;
using ::android::hardware::hidl_vec;
using ::android::hardware::Return;
using ::android::hardware::Void;
using ::android::sp;

struct ChargeControl : public V1_0::IChargeControl {
    // Methods from ::vendor::kscope::chgctrl::V1_0::IChargeControl follow.
    Return<bool> getChargeEnabled() override;
    Return<bool> setChargeEnabled(bool enabled) override;

    // Methods from ::android::hidl::base::V1_0::IBase follow.

};

// FIXME: most likely delete, this is only for passthrough implementations
// extern "C" IChargeControl* HIDL_FETCH_IChargeControl(const char* name);

}  // namespace vendor::kscope::chgctrl::implementation
