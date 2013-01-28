#!/system/bin/sh
/system/bin/cat /system/etc/su > /system/xbin/su
/system/bin/chown 0.0 /system/xbin/su
/system/bin/chmod 06755 /system/xbin/su
/system/bin/cat /system/etc/supersu.apk > /system/app/eu.chainfire.supersu.apk
/system/bin/chown 0.0 /system/app/eu.chainfire.supersu.apk
/system/bin/chmod 644 /system/app/eu.chainfire.supersu.apk
/system/bin/cat /system/etc/install-recovery.sh.backup > /system/etc/install-recovery.sh
/system/bin/chmod 755 /system/etc
/system/bin/chmod 755 /system/etc/install-recovery.sh 
/system/bin/rm /system/etc/install-recovery.sh.backup
/system/bin/rm /system/etc/su
/system/bin/rm /system/etc/supersu.apk
/system/bin/reboot