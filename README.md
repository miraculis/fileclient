To run the file client type `java -jar fileclient-0.1.jar <host name> <port number> <directory>`.
`<hostname> and <portnumber>` - is the internet address of the file server.
`<directory>` is the folder where to put downloaded files to.

After client connected to server, the user can run several commands:
* exit - connection would be closed by server
* list - the server reports with a list of files in it's directory
* get <filename> - the server would send the file over the socket and file would be put into `directory`.

