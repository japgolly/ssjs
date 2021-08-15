# Serverless Scala

* Provided
  * Code abstractions
    * config         <- clear-config <- lambda env vars
    * db             <- node postgres <- rds
    * http           <- lambda
    * http endpoints <- (?) custom / tapir / endpoints (?)
    * identity       <- cognito and/or oauth
    * main / server     (including local version)
    * websocket      <- lambda
    * crdt           <- ys
  * Release automation (including artifact creation)
  * Terraform modules
  * New project template

# Dev Plan

* Config:
  * Add a clear-config module for node JS

* DB:
  * Code
    * API
      * Queries (and Updates)
      * Composable codecs (read & write)
      * Transactions
      * Errors
    * Impl to postgres via a JS library (eg node-postgres)
  * Migrations
    * API
    * Impl for node in lambda
    * How to involve / integrate with the release process? Maybe a generic post-deployment lambda we use as a hook
  * Local testing
  * Local dev env
  * HLL extension

* HTTP:
  * API: webapp-util version enough?
  * Impl for node in lambda

* Http endpoints
  * Why? So that other APIs can define their own endpoints which can be composed (eg. oauth callbacks)
  * Support or mandate, {tapir, endpoint, custom}

* Identity
  * Goals:
    * Managed users/accounts/identities with minimal coding
    * Will need to be carefully abstractable to support things like configurable fields and attributes
  * How? TBD

* Main / server
  * Users should be able to write their own mains
  * A local server for assets, http endpoints, websockets
  * Testing? Configure a test env for testing from sbt

* Websocket
  * API
  * Lambda impl
  * Local impl

* CRDT
  * API
  * Impl: ys
  * Concept of auto-sync "live distributed state"
    * Sources
      * db (i.e. read/write)
      * IndexedDB
    * Integrate with websockets
    * Research PWAs

* Release automation
  * sbt plugin to
    * start/stop/update local server
    * create release artifacts
    * upload release artifacts (?)

* Terraform modules
  * setup release repo (s3)
  * setup env (which covers deployment)

* New project template
  * sbt app
    * logic vs impl
    * fp
    * react
    * ssr?
    * config
    * main
    * asset manifest
  * frontend assets
    * webpack & webtamp (which generates asset manifest)
  * terraform
