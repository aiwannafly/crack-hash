import hashlib
import json
import subprocess
import sys
from json import JSONDecodeError
from typing import List


class CrackStatus:
    value: str
    data: List[str]

    def __init__(self, value: str, data: List[str]):
        self.value = value
        self.data = data

    def __repr__(self):
        return f'CrackStatus(value={self.value}, data={self.data})'


def send_crack_request(md5_hash: str, max_len: int) -> str:
    request = "'{" + f'"hash":"{md5_hash}","maxLength":"{max_len}"' + "}'"
    command = f'curl -s -X POST http://localhost:8090/api/hash/crack -d {request} -H "Content-Type: application/json"'

    with subprocess.Popen(command, shell=True, stdout=subprocess.PIPE).stdout as f:
        try:
            return json.load(f)['requestId']
        except JSONDecodeError:
            raise IOError("Failed to parse response.")


def check_status(request_id: str) -> CrackStatus:
    command = f'curl -s -X GET http://localhost:8090/api/hash/status?requestId={request_id}'

    with subprocess.Popen(command, shell=True, stdout=subprocess.PIPE).stdout as f:
        try:
            response = json.load(f)
        except JSONDecodeError:
            raise IOError("Failed to parse response.")

    return CrackStatus(response['status'], response['data'] if 'data' in response else [])


def get_md5(value: str) -> str:
    return hashlib.md5(value.encode()).hexdigest()


def main():
    argv = sys.argv

    if len(argv) < 2:
        print('usage: crack.py <string> <max_len: optional>')

    string = argv[1]

    if len(argv) == 3:
        max_len = int(argv[2])
    else:
        max_len = len(string)

    request_id = send_crack_request(get_md5(string), max_len)

    print(f'Request id: {request_id}')


if __name__ == '__main__':
    main()
