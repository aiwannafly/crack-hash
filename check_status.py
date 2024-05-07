import sys

from crack import check_status


def main():
    argv = sys.argv

    if len(argv) < 2:
        print('usage: check_status.py <request-id>')

    request_id = argv[1]

    status = check_status(request_id)

    print(status)


if __name__ == '__main__':
    main()
