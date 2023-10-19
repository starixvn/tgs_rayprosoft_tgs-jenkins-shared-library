def call(branch) {
    switch (branch) {
        case ['master', 'origin/master']:
            return "prod"
        case ['release/current', 'origin/release/current']:
            return "staging"
        default:
            return "dev"
    }
}