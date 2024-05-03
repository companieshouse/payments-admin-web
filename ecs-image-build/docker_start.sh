#!/bin/bash
#
# Start script for payments-admin-web

#!/bin/bash

PORT=8080

exec java -jar -Dserver.port="${PORT}" payments-admin-web.jar"