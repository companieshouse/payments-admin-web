#!/bin/bash
#
# Start script 

PORT=8080

exec java -jar -Dserver.port="${PORT}" "payments-admin-web.jar"⏎    