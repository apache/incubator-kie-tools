/**
 * ErrorProcessor runs selenium code and creates detailed stack trace which is swallowed otherwise.
 */
export default class ErrorProcessor {

    /**
     * Runs specified asynchonous selenium function and produces error with detailed stack trace.
     * @param fn Asychonous function to be run.
     * @param errorMessage Error message to be displayed together with detailed stack trace if function fails.
     */
    public static async run<T>(fn: () => Promise<T>, errorMessage: string): Promise<T> {

        // error must be created before selenium fails, otherwise the stack trace is swallowed
        const customError: Error = new Error(errorMessage);

        try {
            // call the function
            return await fn();
        } catch (err) {
            // print detailed stack trace
            console.error(customError.stack);
            throw err;
        }
    }
}
