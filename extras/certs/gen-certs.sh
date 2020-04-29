#!/usr/bin/env bash
#
# Generate self signed certificate files
#

readonly project_name="amforeas"
readonly longevity="36500"

printf "Generate key (${project_name}-root.key) and cert root (${project_name}-root.crt).\n"
openssl req -newkey rsa:2056 -nodes -keyout "${project_name}-root.key" -x509 -days "${longevity}" -out "${project_name}-root.crt" > /dev/null 2>&1 <<EOF
ES
Madrid
Madrid
Amforeas
Test
Amforeas ES
testrootca@amforeas.com


EOF

printf "Generate private key ${project_name}-key.pem\n"
openssl genrsa -out "${project_name}-key.pem" 2056 > /dev/null 2>&1

printf "Generate CSR ${project_name}-cert.csr\n"
openssl req -new -key "${project_name}-key.pem" -out "${project_name}-cert.csr" -config ca.cfg -batch -sha256 > /dev/null 2>&1

printf "Sign the cert to ${project_name}-cert.pem\n"
openssl x509 -req -days "${longevity}" -in "${project_name}-cert.csr" -sha256 -CA "${project_name}-root.crt" -CAkey "${project_name}-root.key" -CAcreateserial -out "${project_name}-cert.pem" -extensions v3_req -extfile ca.cfg > /dev/null 2>&1

printf "Tranform ${project_name}-key.pem to pkcs8 ${project_name}-private-key.pem\n"
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in "${project_name}-key.pem" -out "${project_name}-private-key.pem" > /dev/null 2>&1

printf "Generate PKCS12 ${project_name}-private-key-pkcs12.pem\n"
openssl pkcs12 -export -name "testrootca@${project_name}.com" -in "${project_name}-root.crt" -inkey "${project_name}-root.key" -out "${project_name}-private-key-pkcs12.pem"

printf "Genera public key ${project_name}-public-key.pem\n"
openssl rsa -in $"{project_name}-private-key.pem" -outform PEM -pubout -out "${project_name}-public-key.pem" > /dev/null 2>&1

rm ./*csr
rm ./*srl

printf "Generate cert chain file ${project_name}-cert-chain.pem\n"
cp "${project_name}-cert.pem" "${project_name}-cert-chain.pem"

printf "Generate Java keystore at "${project_name}.jks"\n"
keytool -importkeystore -srckeystore "${project_name}-private-key-pkcs12.pem" -srcstoretype PKCS12 -destkeystore "${project_name}.jks" -deststoretype JKS

printf "Done\n"
exit 0
