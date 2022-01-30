from operator import contains
import random

MAX_TRIES = 6
random.seed()

# Build word list
word_list = []
with open('./words.txt') as file:
    for line in file:
        if len(line) > 5:
            word_list.append(line[:len(line) - 1])
        else:
            word_list.append(line)

wrong_letters = []
correct_letters = []
correct_placements = [None, None, None, None, None]


def random_word(words):
    return words[random.randint(0, len(words) - 1)]


def check_word(word, wordle):
    global correct_letters
    global correct_placements

    for i in range(5):
        if wordle[i] == word[i]:
            correct_placements[i] = word[i]
        elif word[i] in wordle:
            correct_letters.append(word[i])
        else:
            wrong_letters.append(word[i])
    return word == wordle


def filtered_word_list(words):
    global correct_letters
    global correct_placements
    global wrong_letters

    filtered_list = []

    for word in words:
        contains_wrong_letter = False
        for letter in wrong_letters:
            if letter in word:
                contains_wrong_letter = True
                break
        if contains_wrong_letter:
            continue

        flag = True
        for i in range(5):
            if correct_placements[i] is not None and word[i] != correct_placements[i]:
                flag = False
        if flag:
            if all(c in word for c in correct_letters):
                filtered_list.append(word)
                continue

    return filtered_list


def simulate(word_list, first_word):
    wordle = random_word(word_list)
    words = word_list

    word = first_word
    score = 6

    for i in range(6):
        if check_word(word, wordle):
            break
        words = filtered_word_list(words)
        word = random_word(words)
        score -= 1
    return score


print('Running...')

SIMS_PER_WORD = 10

scores = [0] * len(word_list)

for i in range(len(word_list)):
    word = word_list[i]
    score = 0
    for j in range(SIMS_PER_WORD):
        # reset stuff before each game
        wrong_letters = []
        correct_letters = []
        correct_placements = [None, None, None, None, None]
        # play game and note score
        score += simulate(word_list, word)
    scores[i] = score / SIMS_PER_WORD
    print(word, scores[i])

print('Complete!')

best = 0
for i in range(len(word_list)):
    if (scores[best] < scores[i]):
        best = i
print('Best Word:', word_list[best], 'with a score of', scores[best])
