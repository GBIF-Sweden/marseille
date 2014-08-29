marseille
=========
Marseille is a web application written in Java with Play framework 2.2. 

It is a simple application for managing organisations/institutions, collections 
and dataset a GBIF node (or another organisation) works with.

The application is in an early stage, and not ready for production.

Requirement
===========

* MySQL and Java JDK 1.7 and an internet connection.
* Play framework version 2.2.x (<a href="http://www.playframework.com/download#older-versions">http://www.playframework.com/download#older-versions</a>). Please note that as of today dijon DOESN'T WORK with Play 2.3.X
* Apache Tomcat 7 (<a href="http://tomcat.apache.org/">http://tomcat.apache.org/</a>)

Installation
============
Although Play framework applications can run by themselves, it has been decided to make Marseille a usual web application run by Tomcat, hence the conversion to a WAR file later.

Your application needs too to access your Bourgogne database. Open the file <code>marseille/conf/application.conf</code> and find the section "Database configuration". There you must change the user name (in <code>db.default.user</code>) and password (in <code>db.default.password</code>) for making Marseille access your database.

You must as well decides which name you will give to your Marseille installation. It can be as simple as "marseille", or "metadata", or "whateveryoulike". In the same file as before you should find the variable <code>application.context</code> and change it value according to what you just choose. It is very important otherwise the application won't work.

Download Play framework and install it. On GNU/Linux change the PATH from the command line: <code>$ export PATH=$PATH:/path/to/play-2.2.X</code>

You can then move the directory of Marseille, then start Play and then create a WAR file: <code>$ play [marseille]$ war</code>

You can then rename the file <code>/path/to/marseille/target/marseille-1.0-SNAPSHOT.war</code> to <code>marseille.war</code> or <code>metadata.war</code> or <code>whateveryoulike.war</code> as you decided before, and upload it to your Tomcat webserver.


Post-installation
=================
The first user created is admin@gbif.org with the hard-to-guess password "secret". Please change this to anything that fits your taste.

To dos
======
* Integration of the datasets with the one stored in Bourgogne (same ids)
* Import data to Bourgogne and index it in Izeure directly from <code>marseille/datasets</code> when logged in as an administrator.
