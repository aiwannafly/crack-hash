import json
import os
import subprocess
from time import sleep
from typing import List

from tqdm import tqdm


class CrackStatus:
    value: str
    data: List[str]

    def __init__(self, value: str, data: List[str]):
        self.value = value
        self.data = data

    def __repr__(self):
        return f'CrackStatus(value={self.value}, data={self.data})'


def send_crack_request(hash_string: str, max_len: int) -> str:
    request = "'{" + f'"hash":"{hash_string}","maxLength":"{max_len}"' + "}'"
    command = f'curl -s -X POST http://localhost:8090/api/hash/crack -d {request} -H "Content-Type: application/json"'

    with subprocess.Popen(command, shell=True, stdout=subprocess.PIPE).stdout as f:
        return json.load(f)['requestId']


def check_status(request_id: str) -> CrackStatus:
    command = f'curl -s -X GET http://localhost:8090/api/hash/status?requestId={request_id}'

    with subprocess.Popen(command, shell=True, stdout=subprocess.PIPE).stdout as f:
        response = json.load(f)

    return CrackStatus(response['status'], response['data'] if 'data' in response else [])


def stop_container(name: str):
    os.system(f'docker-compose stop {name}')


def start_container(name: str):
    os.system(f'docker-compose start {name}')


def wait(wait_time: int):
    for i in tqdm(range(wait_time), desc=f'Sleeping for {wait_time} secs'):
        sleep(1)

def manager_rabbit_shutdown_scenario():
    hash_string = '0cd0c4aab9323c79aebc7350edf58763'
    max_len = 5

    request_id = send_crack_request(hash_string, max_len)

    sleep(1)

    status = check_status(request_id)

    print(status)

    stop_container('manager')

    stop_container('worker3')

    stop_container('mongodb-primary')

    wait(60)

    stop_container('rabbitmq')

    wait(5)

    start_container('rabbitmq')

    wait(5)

    start_container('manager')

    wait(10)

    status = check_status(request_id)

    start_container('worker3')

    start_container('mongodb-primary')

    print(status)


def main():
    manager_rabbit_shutdown_scenario()

    # stop_container('manager')
    #
    # start_container('manager')

    # request_id = send_crack_request()
    #
    # for i in range(6):
    #     sleep(1)
    #
    #     status = check_status(request_id)
    #
    #     print(status)


if __name__ == '__main__':
    main()
