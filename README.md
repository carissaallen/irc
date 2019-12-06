# Internet Relay Chat
> A simple Internet Relay Chat (IRC) protocol by which clients can communicate with each other.   

[![Build Status](https://travis-ci.com/carissaallen/irc.svg?token=mazRg9fgNkq1HJ56kVyT&branch=master)](https://travis-ci.com/carissaallen/irc)

This communication system employs a central server which _relays_ messages to other connected users. Users can join rooms, which are groups of users that are subscribed to the same message stream. Any message sent to that room is forwarded to all users currently joined to that room.

Users can also send private messages directly to other users. 

## Install & Run

Clone the repository: 
```
git@github.com:carissaallen/irc.git
```

Build the application:
```
mvn clean verify
```

#### Run

1. Start the server: `Run 'Server.main()'`
2. Enter a valid port number and hit start
3. Start the client: `Run 'Client.main()'`
4. Enter a username and hit start

## Technology

* JDK 11
* Maven

## Testing

Testing was not implemented for this iteration. 

## Credits

The RFC document was based on [this](https://github.com/carissaallen/irc/blob/master/docs/Sample_RFC.pdf) example provided by the instructor.

## Built By

* **Mack Cooper** - [@mackkcooper](https://github.com/mackkcooper)

* **Carissa Allen** - [@carissaallen](https://github.com/carissaallen)

## License
Distributed under the MIT License. See [LICENSE](/LICENSE) for more information.

