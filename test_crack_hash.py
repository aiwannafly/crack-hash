import os
from time import sleep

from colorama import Style, Fore
from rich.progress import Progress

from crack import CrackStatus, check_status, send_crack_request, calc_md5


def wait_for_completion(request_id: str, wait_time: int = 60) -> CrackStatus:
    with Progress() as progress:
        wait_task = progress.add_task(description='Wait for response...', total=wait_time)

        for _ in range(wait_time):
            sleep(1)

            progress.update(wait_task, description='Wait for response...', advance=1)

            try:
                status = check_status(request_id)
            except IOError:
                continue

            if status.value == 'ERROR':
                progress.remove_task(wait_task)

                raise ValueError('Failed to get response.')
            elif status.value == 'READY':
                progress.remove_task(wait_task)

                return status

        progress.remove_task(wait_task)
        raise ValueError('Timeout error: could not get response.')


def stop_container(name: str):
    os.system(f'docker-compose stop {name}')


def start_container(name: str):
    os.system(f'docker-compose start {name}')


def wait(wait_time: int):
    with Progress() as progress:
        wait_task = progress.add_task(description='Waiting...', total=wait_time)

        for _ in range(wait_time):
            sleep(1)

            progress.update(wait_task, advance=1, description='Waiting...')

        progress.remove_task(wait_task)


class TestCrackHash:
    def test_simple(self):
        value = 'a5BG'

        request_id = send_crack_request(md5_hash=calc_md5(value), max_len=4)

        status = wait_for_completion(request_id, wait_time=10)

        self.check_response(value, status)

        self.log_separator()

    def test_many_values(self):
        values = ['hi', 'Bob', 'how', 'are', 'you']

        request_ids = [send_crack_request(md5_hash=calc_md5(v), max_len=4) for v in values]

        for value, request_id in zip(values, request_ids):

            status = wait_for_completion(request_id, wait_time=10)

            self.check_response(value, status)

        self.log_separator()

    def test_shutdown_rabbit_first(self):
        value = 'MITM'

        self.log('Stopping rabbitmq...')

        stop_container('rabbitmq')

        request_id = send_crack_request(md5_hash=calc_md5(value), max_len=4)

        self.log(f'Sent request to crack {value}')

        self.log('Restarting rabbitmq...')

        start_container('rabbitmq')

        status = wait_for_completion(request_id, wait_time=20)

        self.check_response(value, status)

        self.log_separator()

    def test_shutdown_manager_worker_rabbit_mongo(self):
        value = 'MONGO'

        request_id = send_crack_request(md5_hash=calc_md5(value), max_len=5)

        self.log(f'Sent request to crack {value}')

        sleep(1)

        self.log(f'Stopping services...')

        stop_container('manager')

        stop_container('worker3')

        stop_container('mongo-primary')

        wait(60)

        stop_container('rabbitmq')

        self.log(f'Restarting services...')

        start_container('rabbitmq')

        wait(5)

        start_container('manager')

        status = wait_for_completion(request_id, wait_time=10)

        self.check_response(value, status)

        start_container('worker3')

        wait(5)

        start_container('mongo-primary')

        self.log_separator()

    def check_response(self, value: str, status: CrackStatus):
        self.assert_in(value, status.data, f'Not found {value} in {status.data}')

        self.log(f"{Fore.LIGHTGREEN_EX}SUCCESS{Style.RESET_ALL}: Found '{value}' in {status}.")

    def log_separator(self):
        print(f'{Style.BRIGHT}----------------------------------------------------------------------{Style.RESET_ALL}\n')

    def log(self, message: str):
        print(f'{Fore.MAGENTA}[ TESTS ]{Style.RESET_ALL}: {message}\n')

    def fail(self, msg=None):
        raise ValueError(msg)

    def assert_equal(self, value1, value2, msg=None):
        if value1 != value2:
            raise self.fail(msg)

    def assert_in(self, value, values, msg=None):
        if value not in values:
            raise self.fail(msg)

    def assert_true(self, value, msg=None):
        if not value:
            raise self.fail(msg)
