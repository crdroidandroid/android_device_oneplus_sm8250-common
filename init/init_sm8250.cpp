/*
   Copyright (c) 2020, The LineageOS Project
                 2020, The Potato Open Sauce Project

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
#include <stdlib.h>
#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <stdio.h>
#include <sys/_system_properties.h>
#include <sys/sysinfo.h>
#include <sys/system_properties.h>

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

void load_12gb() {
  property_override("dalvik.vm.heapstartsize", "24m");
  property_override("dalvik.vm.heapgrowthlimit", "384m");
  property_override("dalvik.vm.heaptargetutilization", "0.42");
  property_override("dalvik.vm.heapmaxfree", "56m");
}

void load_8gb() {
  property_override("dalvik.vm.heapstartsize", "24m");
  property_override("dalvik.vm.heapgrowthlimit", "256m");
  property_override("dalvik.vm.heaptargetutilization", "0.46");
  property_override("dalvik.vm.heapmaxfree", "48m");
}

void load_6gb() {
  property_override("dalvik.vm.heapstartsize", "16m");
  property_override("dalvik.vm.heapgrowthlimit", "256m");
  property_override("dalvik.vm.heaptargetutilization", "0.5");
  property_override("dalvik.vm.heapmaxfree", "32m");
}

/* Check RAM size for current variant. */
void checkram_loadprops() {
  struct sysinfo sys;
  sysinfo(&sys);
  if (sys.totalram > 8192ull * 1024 * 1024) {
    load_12gb();
  } else if (sys.totalram > 6144ull * 1024 * 1024) {
    load_8gb();
  } else if (sys.totalram > 4096ull * 1024 * 1024) {
    load_6gb();
  }
}

void vendor_load_properties() {
  checkram_loadprops();
}
