set idleTime to (do shell script "ioreg -c IOHIDSystem | awk '/HIDIdleTime/ {print $NF/1000000; exit}'")
do shell script "echo " & idleTime
