echo "deploying...%1 to %3"
curl -v -u %4:%5 -F file=@%1 %3?name=%2 -F install=true