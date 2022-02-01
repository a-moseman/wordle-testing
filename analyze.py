from operator import truediv
import matplotlib.pyplot as plt
import csv

x = []
y = []

with open('results.csv', mode='r') as file:
    csvFile = csv.reader(file)
    for row in csvFile:
        x.append(row[0])
        y.append(float(row[1]))

y, x = zip(*sorted(zip(y, x), reverse=True))

x_coords = []
for i in range(len(x)):
    x_coords.append(i)
start = len(x) - 25
plt.bar(x_coords[start:len(x)], y[start:len(x)], tick_label=x[start:len(x)])

plt.xticks(rotation=90)
plt.ylim(min(y[start:len(x)]) - 0.05, max(y[start:len(x)]) + 0.05)

plt.title("First 25 best first words for wordle (5000 sims per word)")
plt.xlabel("First Word")
plt.ylabel("Mean Guesses")

plt.show()
