# Create Laminar App

I wanted to create a similar experience from [create-react-app](https://create-react-app.dev/) for a full-stack Scala project that includes:

- A web application that uses [Laminar](https://laminar.dev/) for Functional Reactive UI which reloads automatically using [vite.js](https://vitejs.dev/)
- A server module that uses [ZIO HTTP](https://zio.dev/zio-http/) for the API, [ZIO](https://zio.dev/) and [ZIO Streams](https://zio.dev/reference/stream/) for functional effects and streams and [Caliban](https://ghostdogpr.github.io/caliban/) for GraphQL.
- A shared module that both the web and server can use

This is very much an opinionated build but hopefully it will help you to set up an environment in no time and get to work.

# How to run it

First of all clone the project using `git clone git@github.com/kossalw/create-laminar-app.git`.

Then you can run the project in two ways. The first one is using Nix to manage dependencies and is the easiest to get running, the second way is by installing dependencies yourself.

## Nix

Install [Nix](https://nixos.org/) by following the [instructions](https://nixos.org/download/).

PD: If you use [Fish shell](https://fishshell.com/) then use this script instead:

```bash
# Install the fisher plugin
curl -sL https://raw.githubusercontent.com/jorgebucaran/fisher/main/functions/fisher.fish | source && fisher install jorgebucaran/fisher

# Install nix
curl -L https://nixos.org/nix/install | sh

# Exports nix binaries to path in the fish way
fisher install lilyball/nix-env.fish
```

Go to the cloned directory (`cd create-laminar-app`) and run `./devserver`, this script runs a nix-shell to:
- Install project dependencies (like java)
- Start a Vite.js server for live reloading
- Start the http server

At the end you should see a Vite.js prompt with a localhost URL where your app is being served, open the browser and go there to ensure everything is working. The first time you run `./devserver` can take 5-10 minutes due to package installation but it get's cached for next time.

The nix config includes [java](https://www.azul.com/), [mill](https://mill-build.com/mill/Intro_to_Mill.html) and [nodejs](https://nodejs.org/) (with [npm](https://www.npmjs.com/)). If you want to run these packages you have two options:

1. Using a nix-shell command like `nix-shell --run "npm install"`
2. Enter a shell where the above programs are available on the PATH:

```bash
# Enter a shell where the programs are available
nix-shell

# Now you have npm and can use it
npm install

# Return to your previous shell
exit
```

## Dependencies

You can use your own jdk, mill and nodejs. Just change the first lines of `devserver`:

```bash
# Change this part:

#!/usr/bin/env nix-shell
#! nix-shell -i bash --pure

# To this:

#!/bin/bash
```

This change removes nix-shell as the program that runs the `devserver` script. Now you can run `./devserver` and it should work the same as with Nix.

## IDE

To leverage the static types it's best to use an IDE that allows for autocompletition and go-to definitions. Good options are:

- [VSCode](https://code.visualstudio.com/) using [metals](https://scalameta.org/metals/docs/editors/vscode/)
- [Intellij Idea](https://www.jetbrains.com/idea/) using the [Scala plugin](https://plugins.jetbrains.com/plugin/1347-scala). For autocompletion you can use [BSP](https://mill-build.com/mill/Installation_IDE_Support.html#_build_server_protocol_bsp) or generate an [idea directory](https://mill-build.com/mill/Installation_IDE_Support.html#_intellij_idea_support).

## Scalablytyped

This project includes [Scalablytyped](https://scalablytyped.org/) to convert [Typescript](https://www.typescriptlang.org/) to Scala types. In my opinion it's a good idea but in practice most times is less productive then to use [Scala.js facades](https://www.scala-js.org/doc/interoperability/facade-types.html). Anyway it's used and available but you can remove it by searching and deleting this line on the **builde** folder:

```scala
def moduleDeps = Seq(`scalablytyped-module`)
```

## Docker

The server module can be built into a docker image using `mill -j 0 server.docker.build`. You'll need to have docker cli (the nix-shell includes it) and a docker server installed (you could install [Docker Desktop](https://www.docker.com/products/docker-desktop/)).

To change the image, tag, repository or any option of the docker build, go to `builder/Server.sc`.

## TODO

If you want to contribute consider these tasks:

- [ ] Add web tests
- [ ] Create GraphQL server with logging and prometheus
- [ ] Add server tests