Xform-Live
==========

About
-----
Xform-Live is a live transformation viewer. Xform-Live is a webapp that runs
locally. Once configured, Xform-Live will display a list of XML files and a
list of XSLT transformations. Select the XML file, and transformation(s), and
Xform-Live shows the effect of the transformation(s).

Xform-Live also supports automatically updating the results periodically. This
allows transformation workflows with quick turn-around time: edit the
transformation, and see the results of the edit nearly instaneously.

Xform-Live is licensed under GPLv3 (see `LICENSE.txt` for details).

Usage
-----
Simply invoke

    mvn jetty:run

and then go to `localhost:8080` in a web browser. From there, configure the
tool, and then use as prescribed above.

*Xform-Live is intended to be run locally only.* Xform-Live requires some
modifications to be used as a hosted tool (to secure file access, etc).

Implementation
--------------
Xform-Live is implemented as an Angular app that talks to RESTful services.

Technologies used:

* [RESTEasy](http://resteasy.jboss.org/)
* [Jackson](https://github.com/FasterXML/jackson)
* [Jetty](http://www.eclipse.org/jetty/)
* [jQuery](http://jquery.com/)
* [Bootstrap](http://getbootstrap.com/)
* [AngularJS](https://angularjs.org/)
* [Angular UI](http://angular-ui.github.io/)
* [ACE Code Editor](http://ace.c9.io/)
