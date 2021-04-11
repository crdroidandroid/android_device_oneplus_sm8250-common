/*
   Copyright (c) 2020, The LineageOS Project

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions are
   met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of The Linux Foundation nor the names of its
      contributors may be used to endorse or promote products derived
      from this software without specific prior written permission.

   THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
   WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
   ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
   BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
   CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
   SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
   BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
   OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
   IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include <android-base/properties.h>
#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <stdio.h>
#include <stdlib.h>
#include <sys/sysinfo.h>
#include <sys/system_properties.h>
#include <sys/_system_properties.h>

#include "property_service.h"
#include "vendor_init.h"

using android::base::GetProperty;

void property_override(char const prop[], char const value[]) {
  prop_info *pi;

  pi = (prop_info *)__system_property_find(prop);
  if (pi)
    __system_property_update(pi, value, strlen(value));
  else
    __system_property_add(prop, strlen(prop), value, strlen(value));
}

void load_dalvikvm_properties() {
  struct sysinfo sys;
  sysinfo(&sys);
  if (sys.totalram > 8192ull * 1024 * 1024) {
    // from - phone-xhdpi-12288-dalvik-heap.mk
    property_override("dalvik.vm.heapstartsize", "24m");
    property_override("dalvik.vm.heapgrowthlimit", "384m");
    property_override("dalvik.vm.heaptargetutilization", "0.42");
    property_override("dalvik.vm.heapmaxfree", "56m");
    }
  else if(sys.totalram > 6144ull * 1024 * 1024) {
    // from - phone-xhdpi-8192-dalvik-heap.mk
    property_override("dalvik.vm.heapstartsize", "24m");
    property_override("dalvik.vm.heapgrowthlimit", "256m");
    property_override("dalvik.vm.heaptargetutilization", "0.46");
    property_override("dalvik.vm.heapmaxfree", "48m");
    }
  else {
    // from - phone-xhdpi-6144-dalvik-heap.mk
    property_override("dalvik.vm.heapstartsize", "16m");
    property_override("dalvik.vm.heapgrowthlimit", "256m");
    property_override("dalvik.vm.heaptargetutilization", "0.5");
    property_override("dalvik.vm.heapmaxfree", "32m");
  }
  property_override("dalvik.vm.heapsize", "512m");
  property_override("dalvik.vm.heapminfree", "8m");
}

void vendor_load_properties() {
  int project_name = stoi(android::base::GetProperty("ro.boot.project_name", ""));
  int rf_version = stoi(android::base::GetProperty("ro.boot.rf_version", ""));
  switch(project_name){
    case 19821:
      /* OnePlus 8 */
      switch (rf_version){
        case 11:
          /* China */
          property_override("ro.product.model", "IN2010");
          break;
        case 12:
          /* T-Mobile */
          property_override("ro.product.model", "IN2017");
          break;
        case 13:
          /* India */
          property_override("ro.product.model", "IN2011");
          break;
        case 14:
          /* Europe */
          property_override("ro.product.model", "IN2013");
          break;
        case 15:
          /* Global / US Unlocked */
          property_override("ro.product.model", "IN2015");
          break;
        default:
          /* Generic */
          property_override("ro.product.model", "IN2015");
          break;
      }
      break;
    case 19811:
      /* OnePlus 8 Pro */
      switch (rf_version){
        case 11:
          /* China */
          property_override("ro.product.model", "IN2020");
          break;
        case 13:
          /* India */
          property_override("ro.product.model", "IN2021");
          break;
        case 14:
          /* Europe */
          property_override("ro.product.model", "IN2023");
          break;
        case 15:
          /* Global / US Unlocked */
          property_override("ro.product.model", "IN2025");
          break;
        default:
          /* Generic */
          property_override("ro.product.model", "IN2025");
          break;
      }
      break;
    case 19805:
      /* OnePlus 8T */
      switch (rf_version){
        case 11:
          /* China */
          property_override("ro.product.model", "KB2000");
          break;
        case 12:
          /* T-Mobile */
          property_override("ro.product.model", "KB2007");
          break;
        case 13:
          /* India */
          property_override("ro.product.model", "KB2001");
          break;
        case 14:
          /* Europe */
          property_override("ro.product.model", "KB2003");
          break;
        case 15:
          /* Global / US Unlocked */
          property_override("ro.product.model", "KB2005");
          break;
        default:
          /* Generic */
          property_override("ro.product.model", "KB2005");
          break;
      }
      break;
  }

  // dalvikvm props
  load_dalvikvm_properties();
}
