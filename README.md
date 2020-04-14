# Amazon Devices

Repository for Amazon Devices DAM Application.


## Links

Development environment: http://amazondevices-dev.apps.prod.nuxeo.io/

UAT environment: http://amazondevices-uat.apps.prod.nuxeo.io/

Pre-Prod environment: https://adg.preprod.nuxeocloud.com/

Prod environment:

Studio project: https://connect.nuxeo.com/nuxeo/site/studio/ide?project=amazon-devices

Marketplace package: 

OpenShift Continuous Deployment: https://openshift.dev.nuxeo.io/console/project/cust-amazondevices/overview


## Requirements

This project requires Java 8, Maven 3 and a Mongo database.

Make sure you are granted access to the `amazon-devices` Studio project and have provided your Github account with your SSH public key.

Make sure you have entered your Nuxeo Studio credentials in your Maven settings in `~/.m2/settings.xml` as described in the documentation [here](https://doc.nuxeo.com/studio/maven-integration/).


## Getting started

- Download a Nuxeo Platform distribution 10.10

- Set `$NUXEO_HOME` variable to this distribution:

    `export NUXEO_HOME={{path-to-distrib}} && cd $NUXEO_HOME`

- Register the instance with your Nuxeo Connect account to the Nuxeo Studio `amazon-devices` project in a `dev` instance type:

    `$NUXEO_HOME/bin/nuxeoctl register`

(It generated an `instance.clid` file in `$NUXEO_HOME/nxserver/data/`).

- Install the Amazon Devices Marketplace package:

    `$NUXEO_HOME/bin/nuxeoctl mp-install amazon-devices-package`

- Install the hotfixes (answer `yes` to everything):

    `$NUXEO_HOME/bin/nuxeoctl mp-hotfix`

	- activate the development mode:
`org.nuxeo.dev=true`,

	- activate the extended error log mode: `org.nuxeo.rest.stack.enable=true`.

- Launch the server:

    `$NUXEO_HOME/bin/nuxeoctl start`
    
- Go to http://localhost:8080/nuxeo.

- Fill in the wizard with the wanted configuration:
	- Choose MongoDB as database. You need to have Mongo installed and started on your machine.
	- Your instance should already be registered to the Nuxeo Studio project.
	- Unselect all addons (the wanted addons are already installed via the marketplace package).

You should now be able to browse the Nuxeo Amazon Devices DAM Application. The default credentials to login are configured in the Studio project.

- To stop the server, run:

    `$NUXEO_HOME/bin/nuxeoctl stop`

- To start the server in a console mode and see the logs, run:

    `$NUXEO_HOME/bin/nuxeoctl console`

## Hierarchy of the project

- `amazon-devices-core`: The Nuxeo module for the core logic of the Amazon Devices DAM Application.

- `amazon-devices-package`: The Nuxeo marketplace package for the Amazon Devices DAM Application.


## Building

- Get the source code:

    `git clone git@github.com:nuxeo-projects/amazon-devices.git && cd amazon-devices`

- Build using Maven:

    `mvn clean install`

(It generated a local marketplace package zip in `amazon-devices-package/target/`).

- (See Nuxeo [Core Developer Guide](http://doc.nuxeo.com/x/B4BH) for more instructions and guidelines).


## Licensing

(C) Copyright [Nuxeo SA](http://nuxeo.com/) and others.

All rights reserved.


## About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com).
