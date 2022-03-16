curl http://elizabsn-clouddesk.aka.corp.amazon.com/OSS/src/AWSA9ESDocs/build/server-root/opensearch-service/md.tar.gz -o md.tar.gz
tar -xf md.tar.gz
mv *.md doc_source/
mv doc_source/README.md .
mv doc_source/CONTRIBUTING.md .
mv doc_source/CODE_OF_CONDUCT.md .
rm md.tar.gz