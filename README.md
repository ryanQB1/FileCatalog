# FileCatalog
\begin{itemize}
    \item Connect <host-ip> <portNo> : Initializes the TCP sockets used to send and receive files and notifications
    \item Register <username> <password> : Registers a new account at the server, if a account with that username already exists, the user is not logged in and has to try again.
    \item Deregister <username> <password> : Deletes the user from the database. This does not delete the owned files or the permissions this user had.
    \item Login <username> <password> : Logs in on an already existing account on the server. If the account does not exist, or the password is wrong, the user is not logged in and has to try again.
    \item Listmy : list all the files of which the logged in user is the owner of.
    \item Listread : list all the files of which the logged in user has reading permissions to.
    \item Listwrite : list all the files of which the logged in user has writing/editing permissions to.
    \item Listall : combines all of the three above. List all files with which the user s affiliated.
    \item Help : provides all available commands, as in this list.
    \item Upload <filepath> : uploads a file to the server and creates the file-info for it. filepath has to be an absolute path, or a path from where the application is stored. The file is stored under the same name as the file (ie not under the filepath).
    \item Download <filename>  : An existing file with filename is downloaded from the server.
    \item Notify <filename> <on/off> : turns notifications for a certain file on or off
    \item Delete <filename> : deletes a file
    \item Givenfo <filename> : gives info on a the file with filename
    \item givewriteperm <filename> <username> : gives write-permissions to the user with username
    \item givereadperm <filename> <username> : gives read-permissions to the user with username
    \item givereadpermpublic <filename> <true/false> : gives everyone read-access to file with filename if the argument that is passed is "true". Reverses effect if "false"
    \item givewritepermpublic <filename> <true/false> : gives everyone write-access to file with filename if the argument that is passed is "true". Reverses effect if "false"
    \item viewreadperm <filename> : View all read-permissions that were given to a certain file
    \item viewwriteperm <filename> : View all write-permissions that were given to a certain file
    \item quit : Quits the application
\end{itemize}

don't delete the /client and /server directories
the pom.xml contains repositories that are needed to run this.
The persistence unit xml is also needed and can be found in \src\main\resources\META-INF