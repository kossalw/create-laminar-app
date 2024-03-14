# Create Laminar App

I wanted to create a similar experience from [create-react-app](https://create-react-app.dev/) but for a full-stack Scala project that includes:

- A web application that uses [Laminar](https://laminar.dev/) for Function Reactive UI which reloads automatically using [vite.js](https://vitejs.dev/)
- A server module that uses [ZIO HTTP](https://zio.dev/zio-http/) for the API, [ZIO](https://zio.dev/) and [ZIO Streams](https://zio.dev/reference/stream/) for functional effects and streams and [Caliban](https://ghostdogpr.github.io/caliban/) for GraphQL.
- A shared module that both the web and server can use

This is very much an opinionated build but hopefully it will help you to set up an environment in no time and get to work.

# How to run it

First of all clone the project using `git clone git@github.com/kossalw/create-laminar-app.git`.

Then ensure that you install [Nix](https://nixos.org/) which is a tool that allows for reproducible environments, so what you run in your computer is the same as what we run. Follow the [instructions](https://nixos.org/download/) to install it.

PD: If you use [Fish shell](https://fishshell.com/) then follow this instructions instead:

```bash
# Install the fisher plugin
curl -sL https://raw.githubusercontent.com/jorgebucaran/fisher/main/functions/fisher.fish | source && fisher install jorgebucaran/fisher

# Install nix
curl -L https://nixos.org/nix/install | sh

# Exports nix binaries to path in the fish way
fisher install lilyball/nix-env.fish
```

After that got to the cloned directory `cd create-laminar-app` and run `./devserver`, this script runs a nix-shell to:
- Install project dependencies (like java)
- Start a Vite.js server for live reloading
- Start the http server

At the end you should see a Vite.js prompt with a localhost URL where your app is being served, open the browser and go there to ensure everything is working. The first time you run `./devserver` can take 5-10 minutes due to package installation but it get's cached for next time.

## TODO

- [ ] Better organize build.sc
- [ ] Create a TODO app
- [ ] Finish the GraphQL example
- [ ] Improve README
- [ ] Add IDE section
- [ ] Include a docker and kubernetes preset