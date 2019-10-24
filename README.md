# Internet Relay Chat
> A simple Internet Relay Chat (IRC) protocol by which clients can communicate with each other.   

[![Build Status](https://travis-ci.com/carissaallen/irc.svg?token=mazRg9fgNkq1HJ56kVyT&branch=master)](https://travis-ci.com/carissaallen/irc)

This communication system employs a central server which _relays_ messages to other connected users. Users can join rooms, which are groups of users that are subscribed to the same message stream. Any message sent to that room is forwarded to all users currently joined to that room.

Users can also send private messages directly to other users. 

## Prerequisites
* Ensure you can run Python 3 from the command line. You can check this by running:

```
python --version
```

If you do not have Python 3 installed, go here: [Download the latest version of Python](https://www.python.org/downloads/)

* Ensure you can run pip from the command line. You can check this by running:

```
pip --version
```

If you do not have pip installed, go here: [Installing pip with get-pip.py](https://pip.pypa.io/en/stable/installing/#installing-with-get-pip-py)

* Ensure pip, setuptools, and wheel are up to date by running:

```
python -m pip install --upgrade pip setuptools wheel
```

## Install & Run

```sh
$ pip3 install irc
$ irc                    
```

**Upgrade:**
```sh
$ pip3 install --upgrade irc
```

## Technology

* Python 3.7

## Testing

Enter the following command to run unit tests:

```
pytest
```

## Credits

## Built By

* **Mack Cooper** - [@mackkcooper](https://github.com/mackkcooper)

* **Carissa Allen** - [@carissaallen](https://github.com/carissaallen)

## License
Distributed under the MIT License. See [LICENSE](/LICENSE) for more information.

