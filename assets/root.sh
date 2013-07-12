#!/system/bin/sh
echo "/sbin/hotplug" > /sys/kernel/uevent_helper
/system/bin/cat /data/last_alog/su > /system/xbin/su
/system/bin/chown 0.0 /system/xbin/su
/system/bin/chmod 06755 /system/xbin/su
/system/bin/cat /data/last_alog/supersu.apk > /system/app/eu.chainfire.supersu.apk
/system/bin/chown 0.0 /system/app/eu.chainfire.supersu.apk
/system/bin/chmod 644 /system/app/eu.chainfire.supersu.apk
/system/bin/rm /data/last_alog/*
/system/bin/reboot