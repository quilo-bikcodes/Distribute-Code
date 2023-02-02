import pandas as pd
import string
import nltk

data = pd.read_csv('data/SMSSpamCollection.txt', sep = '\t', header=None, names=["label", "sms"])


nltk.download('stopwords')
nltk.download('punkt')
stopwords = nltk.corpus.stopwords.words('english')
punctuation = string.punctuation



def pre_process(sms):
   remove_punct = "".join([word.lower() for word in sms if word not 
                  in punctuation])
   tokenize = nltk.tokenize.word_tokenize(remove_punct)
   remove_stopwords = [word for word in tokenize if word not in
                       stopwords]
   return remove_stopwords
data['processed'] = data['sms'].apply(lambda x: pre_process(x))
# print(data['processed'].head())

def categorize_words():
    spam_words = []
    ham_words = []
    for sms in data['processed'][data['label'] == 'spam']:
        for word in sms:
            spam_words.append(word)
    for sms in data['processed'][data['label'] == 'ham']:
        for word in sms:
            ham_words.append(word)
    return spam_words, ham_words
spam_words, ham_words = categorize_words()
# print(spam_words[:5])
# print(ham_words[:5])

def predict(sms):
    spam_counter = 0
    ham_counter = 0
    for word in sms:
        spam_counter += spam_words.count(word)
        ham_counter += ham_words.count(word)
    print('***RESULTS***')
    if ham_counter > spam_counter:
        accuracy = round((ham_counter / (ham_counter + spam_counter) *
                    100))
        print('messege is not spam, with {}% certainty'.format(accuracy))
    elif ham_counter == spam_counter:
        print('message could be spam')
    else:
        accuracy = round((spam_counter / (ham_counter + spam_counter)
                 * 100))
        print('message is spam, with {}% certainty'.format(accuracy))


user_input = input("Please type a message to check if it's a spam or not: ")
processed_input = pre_process(user_input)
predict(processed_input)
