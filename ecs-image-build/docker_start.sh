#!/bin/bash
#
# Start script for payments-admin-web

#!/bin/bash

exec java ${JAVA_MEM_ARGS} -jar -Dserver.port="${PORT}" "/opt/payments-admin-web/payments-admin-web.jar"