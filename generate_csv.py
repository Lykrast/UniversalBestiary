import argparse
import generate_base as gb

parser = argparse.ArgumentParser(description='Generate entries for all mobs in a csv and put the files in the correct place')
parser.add_argument('csv', help='Location of the csv file')
parser.add_argument('-f', '--firstline', help='don\'t skip the first line', action='store_true')

args = parser.parse_args()

print('Reading templates')
temp_ach = gb.readTemplate('advancement')
temp_ent0 = gb.readTemplate('entry_csv_0')
temp_ent1 = gb.readTemplate('entry_csv_1')
temp_ent2 = gb.readTemplate('entry_csv_2')
temp_ent3 = gb.readTemplate('entry_csv_3')

with open(args.csv, 'r') as csv:
	#Discard first line
	print('Reading csv')
	if not args.firstline:
		csv.readline()

	line = csv.readline()
	while line:
		#Format: registry|name|category|description|health|damage|drop1|dropdesc1|drop2|dropdesc2|drop3|dropdesc3
		#        0        1    2        3           4      5      6     7         8     9         10    11
		params = line.split('|')

		registry = params[0]
		name = params[1]
		category = params[2]

		if not gb.validCategory(category):
			print('Error making entry for {} ({}): invalid category {} must be a (animals), h (helpers), m (monsters) or b (bosses)'.format(name, registry, category))
			line = csv.readline()
			continue

		print('Making entry for {} ({})'.format(name, registry))

		category = gb.convertCategory(category)
		description = params[3]
		health = params[4]
		damage = params[5]
		filename = gb.regToFilename(registry)

		#Advancement file
		advancement = temp_ach.replace('@MOB@', registry)
		with open('./src/main/resources/assets/universalbestiary/advancements/{}.json'.format(filename), 'w') as file_adv:
			file_adv.write(advancement)

		#Entry file
		#It's all dirty ad hoc good luck future me
		entry = temp_ent0
		itemcount = 0
		if params[6]:
			itemcount = 1
			entry = temp_ent1
			if params[8]:
				itemcount = 2
				entry = temp_ent2
				if params[10]:
					itemcount = 3
					entry = temp_ent3
		entry = entry.replace('@MOB@', registry).replace('@FILE@', filename).replace('@NAME@', name).replace('@CATEGORY@', category).replace('@DESCRIPTION@', description).replace('@HEALTH@', health).replace('@DAMAGE@', damage)
		if itemcount >= 1:
			entry = entry.replace('@ITEM1@', params[6]).replace('@ITEMDESC1@', params[7])
		if itemcount >= 2:
			entry = entry.replace('@ITEM2@', params[8]).replace('@ITEMDESC2@', params[9])
		if itemcount >= 3:
			entry = entry.replace('@ITEM3@', params[10]).replace('@ITEMDESC3@', params[11])

		with open('./src/main/resources/assets/universalbestiary/patchouli_books/bestiary/en_us/entries/{}.json'.format(filename), 'w') as file_ent:
			file_ent.write(entry)

		line = csv.readline()

print('Done!')